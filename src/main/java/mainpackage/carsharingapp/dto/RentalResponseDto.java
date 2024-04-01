package mainpackage.carsharingapp.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Long userId;
    private Long carId;
    private CarResponseDto carResponseDto;
}
