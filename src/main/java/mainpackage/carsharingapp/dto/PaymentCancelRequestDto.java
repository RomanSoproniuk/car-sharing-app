package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelRequestDto {
    private Long paymentId;
    private String sessionId;
}
