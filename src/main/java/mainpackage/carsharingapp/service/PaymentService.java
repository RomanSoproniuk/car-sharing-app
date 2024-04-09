package mainpackage.carsharingapp.service;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.model.Payment;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    BigDecimal calculateMoneyToPay(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto savePayment(Session session, PaymentRequestDto paymentRequestDto)
            throws MalformedURLException;

    Payment updatePaymentStatusSuccess(Long paymentId);

    List<PaymentResponseDto> getPayments(RentalSearchParameters rentalSearchParameters,
                                         Principal principal,
                                         Pageable pageable);
}
