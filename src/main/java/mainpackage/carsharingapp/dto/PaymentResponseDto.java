package mainpackage.carsharingapp.dto;

import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import mainpackage.carsharingapp.model.Payment;

@Setter
@Getter
public class PaymentResponseDto {
    private Long id;
    private Long rentalId;
    private URL sessionUrl; //URL for the payment session with payment provider
    private String sessionId; //ID of the payment session
    private BigDecimal amountToPay; //calculated rental total price
    private Payment.Status status;
    private Payment.Type type;
}
