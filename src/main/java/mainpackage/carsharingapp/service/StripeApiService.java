package mainpackage.carsharingapp.service;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;

public interface StripeApiService {
    PaymentResponseDto createSession(PaymentRequestDto paymentRequestDto)
            throws StripeException, MalformedURLException;
}
