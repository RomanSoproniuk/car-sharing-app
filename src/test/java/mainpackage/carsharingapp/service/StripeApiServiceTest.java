package mainpackage.carsharingapp.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.Payment;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.Role;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.impl.StripeApiServiceImpl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class StripeApiServiceTest {
    @InjectMocks
    private StripeApiServiceImpl service;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PaymentService paymentService;
    private Principal principal;
    private PaymentRequestDto paymentRequestDto;
    private Session session;
    private PaymentResponseDto paymentResponseDto;
    private Rental rental;
    private Car car;
    private User userManager;
    private Role roleManager;
    private Customer customer;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        ReflectionTestUtils.setField(service, "stripeSecretKey",
                "sk_test_51P0kDz2KaTehUIHVJK1RCtpUcDLMnrgx8IitCt1wmBEe"
                + "NLpCH3dN5SYltWu7fpfL5Vo4IjHsge5m7wFaFqHosCIl00AuezDcXJ");
        ReflectionTestUtils.setField(service, "schemeProtocol", "http");
        ReflectionTestUtils.setField(service, "cancelPath", "/payments/cancel");
        ReflectionTestUtils.setField(service, "successPath",
                "/payments/success?session_id={CHECKOUT_SESSION_ID}");
        ReflectionTestUtils.setField(service, "host", "localhost:8080");
        customer = new Customer();
        customer.setEmail("bob@gmail.com");
        customer.setName("Bob");
        car = new Car(1L, "Q8", "Audi", Car.Type.SEDAN,
                5, BigDecimal.valueOf(2000L), false);
        rental = new Rental(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                1L, 2L, false, true);
        session = new Session();
        session.setStatus("open");
        session.setUrl("http://testurl:8080/test");
        session.setAmountSubtotal(2000L);
        session.setId("testId");
        paymentResponseDto = new PaymentResponseDto(1L, 1L, new URL("http://testurl:8080/test"),
                "testId", BigDecimal.valueOf(2000L), Payment.Status.PENDING, Payment.Type.PAYMENT);
        paymentRequestDto = new PaymentRequestDto(1L, Payment.Type.PAYMENT);
        principal = new Principal() {
            @Override
            public String getName() {
                return "bob@gmail.com";
            }
        };
        roleManager = new Role(1L, Role.RoleName.MANAGER);
        userManager = new User(2L, "bob@gmail.com", "Bob", "Alison",
                "12345678", false);
        userManager.getRoles().add(roleManager);
    }

    @Test
    @DisplayName("""
            Test correct creation session
            """)
    public void createSession_CreateCorrectSession_Success()
            throws MalformedURLException, StripeException {
        //given
        Mockito.when(paymentService.calculateMoneyToPay(paymentRequestDto))
                .thenReturn(BigDecimal.valueOf(2000L));
        Mockito.when(paymentService.savePayment(Mockito.any(), Mockito.any()))
                .thenReturn(paymentResponseDto);
        Mockito.when(rentalRepository.findById(Mockito.any())).thenReturn(Optional.of(rental));
        Mockito.when(carRepository.findById(Mockito.any())).thenReturn(Optional.of(car));
        Mockito.when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(userManager));
        PaymentResponseDto expected = paymentResponseDto;
        //when
        PaymentResponseDto actual = service.createSession(paymentRequestDto, principal);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
