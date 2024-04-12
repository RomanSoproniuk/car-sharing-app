package mainpackage.carsharingapp.service;

import com.stripe.model.checkout.Session;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
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
import mainpackage.carsharingapp.service.impl.PaymentServiceImpl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RentalSpecificationBuilder rentalSpecificationBuilder;
    private Specification<Rental> rentalSpecification;
    private Principal principal;
    private Pageable pageable;
    private RentalSearchParameters rentalSearchParameters;
    private PaymentResponseDto responseDto;
    private Long carId;
    private Long rentalId;
    private Long paymentId;
    private PaymentRequestDto paymentRequestDto;
    private PaymentResponseDto paymentResponseDto;
    private Session session;
    private User userManager;
    private User userCustomer;
    private Payment payment;
    private Rental rental;
    private Car car;
    private Role roleManager;
    private Role roleCustomer;
    private Page<Rental> rentalPage;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        payment = new Payment(1L, 1L, new URL("http://testurl:8080/test"), "testId",
                BigDecimal.valueOf(20L), Payment.Status.PAID, Payment.Type.PAYMENT, false);
        session = new Session();
        session.setStatus("open");
        session.setUrl("http://testurl:8080/test");
        session.setAmountSubtotal(20L);
        session.setId("testId");
        carId = 1L;
        rentalId = 1L;
        roleManager = new Role(1L, Role.RoleName.MANAGER);
        roleCustomer = new Role(2L, Role.RoleName.CUSTOMER);
        userManager = new User(2L, "bob@gmail.com", "Bob", "Alison",
                "12345678", false);
        userManager.getRoles().add(roleManager);
        userCustomer = new User(2L, "bob@gmail.com", "Bob", "Alison",
                "12345678", false);
        userCustomer.getRoles().add(roleCustomer);
        rentalSearchParameters = new RentalSearchParameters(new Boolean[]{true},
                new Long[]{2L});
        car = new Car(1L, "Q8", "Audi", Car.Type.SEDAN,
                5, BigDecimal.valueOf(20L), false);
        rental = new Rental(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                1L, 2L, false, true);
        paymentRequestDto = new PaymentRequestDto(1L, Payment.Type.PAYMENT);
        paymentResponseDto = new PaymentResponseDto(1L, 1L, new URL("http://testurl:8080/test"),
                "testId", BigDecimal.valueOf(20L), Payment.Status.PENDING, Payment.Type.PAYMENT);
        pageable = PageRequest.of(0, 5);
        principal = new Principal() {
            @Override
            public String getName() {
                return "bob@gmail.com";
            }
        };
        rentalPage = new PageImpl<>(List.of(rental), pageable, 5);
        rentalSpecification = new Specification<Rental>() {
            @Override
            public Predicate toPredicate(Root<Rental> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };
    }

    @Test
    @DisplayName("""
            Calculate correct money to pay
            """)
    public void calculateMoneyToPay_CorrectCalculateMoney_Success() {
        //given
        BigDecimal expected = BigDecimal.valueOf(20L);
        Mockito.when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        Mockito.when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        //when
        BigDecimal actual = paymentService.calculateMoneyToPay(paymentRequestDto);
        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Correct save payment
            """)
    public void savePayment_CorrectSavePayment_Success() throws MalformedURLException {
        //given
        PaymentResponseDto expected = paymentResponseDto;
        Mockito.when(paymentRepository.save(Mockito.any())).thenReturn(payment);
        Mockito.when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);
        //when
        PaymentResponseDto actual = paymentService.savePayment(session, paymentRequestDto);
        //
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Correct update payment status
            """)
    public void updatePaymentStatusSuccess_CorrectUpdatePaymentStatus_Success() {
        //given
        Payment expected = payment;
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        Mockito.when(paymentRepository.save(Mockito.any())).thenReturn(payment);
        //when
        Payment actual = paymentService.updatePaymentStatusSuccess(paymentId);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            
            """)
    public void getPayments_ReturnCorrectPaymentsBySearchParamsRoleManager_Success() {
        //given
        Mockito.when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(userManager));
        Mockito.when(rentalSpecificationBuilder.build(rentalSearchParameters))
                .thenReturn(rentalSpecification);
        Mockito.when(rentalRepository.findAll(rentalSpecification, pageable))
                .thenReturn(rentalPage);
        Mockito.when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(payment));
        Mockito.when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);
        List<PaymentResponseDto> expectedList = List.of(paymentResponseDto);
        Mockito.when(paymentRepository.findByRentalIds(Mockito.any())).thenReturn(List.of(payment));
        //when
        List<PaymentResponseDto> actualList
                = paymentService.getPayments(rentalSearchParameters, principal, pageable);
        //then
        Assertions.assertEquals(expectedList.size(), actualList.size());
        EqualsBuilder.reflectionEquals(expectedList.get(0), actualList.get(0));
    }

    @Test
    @DisplayName("""
            
            """)
    public void getPayments_ReturnCorrectPaymentsBySearchParamsRoleCustomer_Success() {
        //given
        Mockito.when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(userCustomer));
        Mockito.when(rentalSpecificationBuilder.build(rentalSearchParameters))
                .thenReturn(rentalSpecification);
        Mockito.when(rentalRepository.findAll(rentalSpecification, pageable))
                .thenReturn(rentalPage);
        Mockito.when(paymentRepository.findById(Mockito.any())).thenReturn(Optional.of(payment));
        Mockito.when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);
        List<PaymentResponseDto> expectedList = List.of(paymentResponseDto);
        Mockito.when(paymentRepository.findByRentalIds(Mockito.any())).thenReturn(List.of(payment));
        //when
        List<PaymentResponseDto> actualList
                = paymentService.getPayments(rentalSearchParameters, principal, pageable);
        //then
        Assertions.assertEquals(expectedList.size(), actualList.size());
        EqualsBuilder.reflectionEquals(expectedList.get(0), actualList.get(0));
    }
}
