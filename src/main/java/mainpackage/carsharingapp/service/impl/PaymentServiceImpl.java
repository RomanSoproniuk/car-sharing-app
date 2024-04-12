package mainpackage.carsharingapp.service.impl;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.exceptions.RoleException;
import mainpackage.carsharingapp.mapper.PaymentMapper;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.Payment;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.Role;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.repository.PaymentRepository;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.RentalSpecificationBuilder;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.PaymentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final long FINE_MULTIPLIER = 2;
    private static final long NUMBER_IDS_ALLOWED_SEE_CUSTOMER = 1L;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final PaymentMapper paymentMapper;
    private final UserRepository userRepository;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;

    @Override
    public BigDecimal calculateMoneyToPay(PaymentRequestDto paymentRequestDto) {
        Long rentalId = paymentRequestDto.getRentalId();
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Rental by id" + rentalId
                        + " does not exist"));
        Long carId = rental.getCarId();
        Car carById = carRepository.findById(carId).get();
        BigDecimal moneyToPay = carById.getDailyFee();
        LocalDate returnDate = rental.getReturnDate();
        LocalDate actualReturnDate = rental.getActualReturnDate();
        if (actualReturnDate.isAfter(returnDate)) {
            long betweenReturnAndActualReturnDate = ChronoUnit.DAYS
                    .between(returnDate, actualReturnDate);
            moneyToPay = moneyToPay.multiply(BigDecimal.valueOf(betweenReturnAndActualReturnDate));
        }
        if (paymentRequestDto.getType() == Payment.Type.FINE) {
            moneyToPay = moneyToPay.multiply(BigDecimal.valueOf(FINE_MULTIPLIER));
        }
        return moneyToPay;
    }

    @Override
    public PaymentResponseDto savePayment(Session session, PaymentRequestDto paymentRequestDto)
            throws MalformedURLException {
        Payment payment = new Payment();
        payment.setSessionId(session.getId());
        if (Objects.equals(session.getStatus(), SessionStatus.OPEN.title)) {
            payment.setStatus(Payment.Status.PENDING);
        }
        if (Objects.equals(session.getStatus(), SessionStatus.COMPLETE.title)) {
            payment.setStatus(Payment.Status.PAID);
        }
        payment.setSessionUrl(new URL(session.getUrl()));
        payment.setRentalId(paymentRequestDto.getRentalId());
        payment.setAmountToPay(BigDecimal.valueOf(session.getAmountSubtotal()));
        payment.setType(paymentRequestDto.getType());
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public Payment updatePaymentStatusSuccess(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() ->
                new EntityNotFoundException("Payment by id " + paymentId
                        + " does not exist in DB"));
        payment.setStatus(Payment.Status.PAID);
        return paymentRepository.save(payment);
    }

    @Override
    public List<PaymentResponseDto> getPayments(
            RentalSearchParameters rentalSearchParameters,
            Principal principal,
            Pageable pageable) {
        User user = userRepository.findByEmail(principal.getName()).get();
        Optional<Role> roleManager = user.getRoles().stream()
                .filter(p -> p.getName().equals(Role.RoleName.MANAGER))
                .findFirst();
        Specification<Rental> rentalSpecification
                = rentalSpecificationBuilder.build(rentalSearchParameters);
        if (roleManager.isPresent()) {
            return rentalRepository.findAll(rentalSpecification, pageable).stream()
                    .map(r -> paymentMapper.toDto(paymentRepository.findById(r.getId()).get()))
                    .toList();
        } else {
            if (rentalSearchParameters.usersId().length != NUMBER_IDS_ALLOWED_SEE_CUSTOMER
                    || !Arrays.stream(rentalSearchParameters.usersId()).findFirst()
                    .get().equals(user.getId())) {
                throw new RoleException("You have not access to see not you own payments");
            }
            return rentalRepository.findAll(rentalSpecification, pageable).stream()
                    .map(r -> paymentMapper.toDto(paymentRepository.findById(r.getId()).get()))
                    .toList();
        }
    }

    @Getter
    public enum SessionStatus {
        OPEN("open"),
        COMPLETE("complete");

        private final String title;
        SessionStatus(String title) {
            this.title = title;
        }
    }
}
