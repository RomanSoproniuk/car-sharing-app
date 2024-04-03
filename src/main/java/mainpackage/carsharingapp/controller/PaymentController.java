package mainpackage.carsharingapp.controller;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.service.StripeApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final StripeApiService stripeApiService;

    @PostMapping
    public PaymentResponseDto createSession(@RequestBody PaymentRequestDto paymentRequestDto)
            throws StripeException, MalformedURLException {
        return stripeApiService.createSession(paymentRequestDto);
    }
}
