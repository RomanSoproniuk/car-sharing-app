package mainpackage.carsharingapp.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RentalResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Long userId;
    private Long carId;
    private CarResponseDto carResponseDto;

    public RentalResponseDto() {
    }

    public RentalResponseDto(Long id, LocalDate rentalDate, LocalDate returnDate,
                             LocalDate actualReturnDate, Long userId, Long carId,
                             CarResponseDto carResponseDto) {
        this.id = id;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.userId = userId;
        this.carId = carId;
        this.carResponseDto = carResponseDto;
    }
}
