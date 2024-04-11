package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCancelResponseDto {
    private String message;

    public PaymentCancelResponseDto() {
    }

    public PaymentCancelResponseDto(String message) {
        this.message = message;
    }

}
