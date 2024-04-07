package mainpackage.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mainpackage.carsharingapp.dto.PaymentCancelResponseDto;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.PaymentSuccessfulResponseDto;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.Payment;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.repository.PaymentRepository;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.PaymentService;
import mainpackage.carsharingapp.service.StripeApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class StripeApiServiceImpl implements StripeApiService {
    private static final String USD_CURRENCY = "usd";
    private static final String DEFAULT_CANCEL_MESSAGE
            = "You can pay later (but the session is available for only 24 hours)";
    private static final long TWENTY_FOUR_HOURS_IN_SECONDS = 86399L;
    private static final long DEFAULT_QUANTITY_CAR = 1;
    private static final long NUMBER_OF_MILLISECONDS_IN_ONE_SECOND = 1000;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private UriComponents successUrl;
    private UriComponents cancelUrl;
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${scheme.protocol}")
    private String schemeProtocol;
    @Value("${host}")
    private String host;
    @Value("${success.path}")
    private String successPath;
    @Value("${cancel.path}")
    private String cancelPath;

    @Override
    public PaymentResponseDto createSession(PaymentRequestDto paymentRequestDto,
                                            Principal principal)
            throws StripeException, MalformedURLException {
        successUrl = UriComponentsBuilder.newInstance()
                .scheme(schemeProtocol)
                .host(host)
                .path(successPath)
                .build();
        cancelUrl = UriComponentsBuilder.newInstance()
                .scheme(schemeProtocol)
                .host(host)
                .path(cancelPath)
                .build();
        Stripe.apiKey = stripeSecretKey;
        BigDecimal moneyToPay = paymentService.calculateMoneyToPay(paymentRequestDto);
        SessionCreateParams.LineItem sessionItem = SessionCreateParams.LineItem.builder()
                .setPrice(getPrice(moneyToPay, paymentRequestDto).getId())
                .setQuantity(DEFAULT_QUANTITY_CAR)
                .build();
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(String.valueOf(cancelUrl))
                .setSuccessUrl(String.valueOf(successUrl))
                .addAllLineItem(List.of(sessionItem))
                .setCustomer(createCustomer(principal).getId())
                .setExpiresAt(System.currentTimeMillis() / NUMBER_OF_MILLISECONDS_IN_ONE_SECOND
                        + TWENTY_FOUR_HOURS_IN_SECONDS)
                .build();
        Session session = Session.create(params);
        return paymentService.savePayment(session, paymentRequestDto);
    }

    private Customer createCustomer(Principal principal) throws StripeException {
        User user = userRepository.findByEmail(principal.getName()).get();
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getFirstName() + " " + user.getLastName())
                .setEmail(user.getEmail())
                .build();
        return Customer.create(params);
    }

    @Override
    public PaymentSuccessfulResponseDto checkSuccessfulPayment(
            String sessionId)
            throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        Payment paymentBySessionId = paymentRepository.findPaymentBySessionId(sessionId);
        Session session = Session.retrieve(sessionId);
        Customer customer = Customer.retrieve(session.getCustomer());
        Payment updatedPaymentStatusSuccess
                = paymentService.updatePaymentStatusSuccess(paymentBySessionId
                .getId());
        PaymentSuccessfulResponseDto paymentSuccessfulResponseDto
                = new PaymentSuccessfulResponseDto();
        paymentSuccessfulResponseDto.setCustomerName(customer.getName());
        paymentSuccessfulResponseDto.setStatus(updatedPaymentStatusSuccess
                .getStatus());
        paymentSuccessfulResponseDto.setRentalId(updatedPaymentStatusSuccess
                .getRentalId());
        return paymentSuccessfulResponseDto;
    }

    @Override
    public PaymentCancelResponseDto cancelPayment() {
        return new PaymentCancelResponseDto(DEFAULT_CANCEL_MESSAGE);
    }

    private Price getPrice(BigDecimal moneyToAmount, PaymentRequestDto paymentRequestDto)
            throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        PriceCreateParams params = PriceCreateParams.builder()
                .setCurrency(USD_CURRENCY)
                .setUnitAmount(moneyToAmount.longValue())
                .setProductData(
                        PriceCreateParams.ProductData.builder()
                                .setName(getProduct(paymentRequestDto).getName())
                                .build()
                )
                .build();
        return Price.create(params);
    }

    private Product getProduct(PaymentRequestDto paymentRequestDto)
            throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        Long rentalId = paymentRequestDto.getRentalId();
        Rental rental = rentalRepository.findById(rentalId).get();
        Car car = carRepository.findById(rental.getCarId()).get();

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(car.getBrand() + " " + car.getModel())
                .build();
        return Product.create(params);
    }
}
