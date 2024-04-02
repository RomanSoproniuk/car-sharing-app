package mainpackage.carsharingapp.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
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
    private static final long DEFAULT_QUANTITY_CAR = 1;
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
    private final PaymentService paymentService;

    @Override
    public PaymentResponseDto createSession(PaymentRequestDto paymentRequestDto)
            throws StripeException, MalformedURLException {
        UriComponents successUrl = UriComponentsBuilder.newInstance()
                .scheme(schemeProtocol)
                .host(host)
                .path(successPath)
                .build();
        UriComponents cancelUrl = UriComponentsBuilder.newInstance()
                .scheme(schemeProtocol)
                .host(host)
                .path(cancelPath)
                .build();
        Stripe.apiKey = stripeSecretKey;
        BigDecimal moneyToPay = paymentService.calculateMoneyToPay(paymentRequestDto);
        SessionCreateParams.LineItem sessionItem = SessionCreateParams.LineItem.builder()
                .setPrice(String.valueOf(moneyToPay))
                .setQuantity(DEFAULT_QUANTITY_CAR)
                .build();
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(String.valueOf(cancelUrl))
                .setSuccessUrl(String.valueOf(successUrl))
                .addAllLineItem(List.of(sessionItem))
                .build();
        Session session = Session.create(params);
        return paymentService.savePayment(session, paymentRequestDto);
    }
}
