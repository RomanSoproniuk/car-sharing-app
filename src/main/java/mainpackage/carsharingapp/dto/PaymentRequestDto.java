package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;
import mainpackage.carsharingapp.model.Payment;

@Getter
@Setter
public class PaymentRequestDto {
    private Long rentalId;
    private Payment.Type type;

    public PaymentRequestDto() {
    }

    public PaymentRequestDto(Long rentalId, Payment.Type type) {
        this.rentalId = rentalId;
        this.type = type;
    }
}
