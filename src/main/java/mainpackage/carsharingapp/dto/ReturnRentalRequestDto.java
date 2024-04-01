package mainpackage.carsharingapp.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRentalRequestDto {
    private LocalDate actualReturnDate;
}
