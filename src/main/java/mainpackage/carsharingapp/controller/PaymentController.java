package mainpackage.carsharingapp.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.PaymentCancelResponseDto;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.PaymentSuccessfulResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.service.PaymentService;
import mainpackage.carsharingapp.service.StripeApiService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = """
        This API helps to make rent payments. Its use is safe, 
        because it works according to the legend of a third-party 
        API, and the Stripe API itself. The application does not 
        save personal data of payment instruments, but only directs 
        the user to the page of the third API where he performs all 
        the necessary actions during payment. You are not responsible 
        for personal data when paying a user.
            """)

public class PaymentController {
    private final StripeApiService stripeApiService;
    private final PaymentService paymentService;

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    @PostMapping
    @Operation(summary = "API for creating a payment session", description = """ 
        With the help of this API, you can create a payment session to make a rental payment, 
        only authenticated users have access to this API. Personal data, namely credit 
        cards and their data, are not stored by the program.
            """)
    public PaymentResponseDto createSession(@RequestBody PaymentRequestDto paymentRequestDto,
                                            Principal principal)
            throws StripeException, MalformedURLException {
        return stripeApiService.createSession(paymentRequestDto, principal);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    @GetMapping("/success")
    @Operation(summary = "Redirect API from Stripe payment page "
            + "API after successful payment", description = """ 
        After the user has made a successful payment, he will be redirected 
        from the Stripe API page to this URL, if necessary, the data returned 
        from this API can be customized according to your requirements and needs. 
        Only authenticated users have access
            """)
    public PaymentSuccessfulResponseDto checkSuccessfulPayment(
            @RequestParam("session_id") String sessionId)
            throws StripeException {
        return stripeApiService.checkSuccessfulPayment(sessionId);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    @GetMapping("/cancel")
    @Operation(summary = "Cancel payment API", description = """ 
        If the user for some reason changes his mind about paying the rent, 
        or wants to return to your site, and uses the link on the Stripe API 
        page with the "BACK" key, he will be redirected to this endpoint. I
         must note that the session will not be eliminated, it will be stored 
         for 24 hours, during which the user can return to it again.
            """)
    public PaymentCancelResponseDto cancelPayment() throws StripeException {
        return stripeApiService.cancelPayment();
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    @GetMapping("/search")
    @Operation(summary = "Receive payment according to the parameters", description = """ 
        This endpoint is designed to dynamically search for the required payments using 
        parameters, it is worth noting that users with MANAGER access can see the payments 
        of all users, while users without this access can only see their own payments.
         Unauthenticated users have no access at all.
            """)
    public List<PaymentResponseDto> getPayments(RentalSearchParameters rentalSearchParameters,
                                                Principal principal,
                                                Pageable pageable) {
        return paymentService.getPayments(rentalSearchParameters, principal, pageable);
    }
}
