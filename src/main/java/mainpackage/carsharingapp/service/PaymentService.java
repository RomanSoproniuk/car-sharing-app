package mainpackage.carsharingapp.service;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;

public interface PaymentService {
    BigDecimal calculateMoneyToPay(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto savePayment(Session session, PaymentRequestDto paymentRequestDto)
            throws MalformedURLException;
}
