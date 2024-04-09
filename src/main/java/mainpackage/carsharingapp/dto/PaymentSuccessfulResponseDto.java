package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;
import mainpackage.carsharingapp.model.Payment;

@Getter
@Setter
public class PaymentSuccessfulResponseDto {
    private Payment.Status status;
    private Long rentalId;
    private String customerName;

    public PaymentSuccessfulResponseDto() {
    }

    public PaymentSuccessfulResponseDto(Payment.Status status, Long rentalId, String customerName) {
        this.status = status;
        this.rentalId = rentalId;
        this.customerName = customerName;
    }
}
