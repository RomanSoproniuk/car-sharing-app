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
    private URL sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
    private Payment.Status status;
    private Payment.Type type;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long id, Long rentalId, URL sessionUrl, String sessionId,
                              BigDecimal amountToPay, Payment.Status status, Payment.Type type) {
        this.id = id;
        this.rentalId = rentalId;
        this.sessionUrl = sessionUrl;
        this.sessionId = sessionId;
        this.amountToPay = amountToPay;
        this.status = status;
        this.type = type;
    }
}
