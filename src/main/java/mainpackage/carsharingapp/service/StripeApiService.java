package mainpackage.carsharingapp.service;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import java.security.Principal;
import mainpackage.carsharingapp.dto.PaymentCancelResponseDto;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.PaymentSuccessfulResponseDto;

public interface StripeApiService {
    PaymentResponseDto createSession(PaymentRequestDto paymentRequestDto,
                                     Principal principal)
            throws StripeException, MalformedURLException;

    PaymentSuccessfulResponseDto checkSuccessfulPayment(
            String sessionId)
            throws StripeException;

    PaymentCancelResponseDto cancelPayment() throws StripeException;
}
