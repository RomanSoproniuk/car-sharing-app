package mainpackage.carsharingapp.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalRequestDto {
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private Long carId;
    private Long userId;
}
