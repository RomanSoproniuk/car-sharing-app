package mainpackage.carsharingapp.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRentalResponseDto {
    private LocalDate actualReturnDate;

    public ReturnRentalResponseDto() {
    }

    public ReturnRentalResponseDto(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
}
