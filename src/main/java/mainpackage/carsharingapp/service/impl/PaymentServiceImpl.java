package mainpackage.carsharingapp.service.impl;

import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.mapper.PaymentMapper;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.Payment;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.repository.PaymentRepository;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String SESSION_STATUS_OPEN = "open";
    private static final String SESSION_STATUS_COMPLETE = "complete";

    private static final long FINE_MULTIPLIER = 2;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public BigDecimal calculateMoneyToPay(PaymentRequestDto paymentRequestDto) {
        Long rentalId = paymentRequestDto.getRentalId();
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Rental by id" + rentalId
                        + " does bot exist"));
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
        if (Objects.equals(session.getStatus(), SESSION_STATUS_OPEN)) {
            payment.setStatus(Payment.Status.PENDING);
        }
        if (Objects.equals(session.getStatus(), SESSION_STATUS_COMPLETE)) {
            payment.setStatus(Payment.Status.PAID);
        }
        payment.setSessionUrl(new URL(session.getUrl()));
        payment.setRentalId(paymentRequestDto.getRentalId());
        payment.setAmountToPay(BigDecimal.valueOf(session.getAmountSubtotal()));
        payment.setType(paymentRequestDto.getType());
        Payment savedPayment = paymentRepository.save(payment);
        return paymentMapper.toDto(savedPayment);
    }
}
