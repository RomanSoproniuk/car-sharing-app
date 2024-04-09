package mainpackage.carsharingapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import mainpackage.carsharingapp.model.Car;

@Getter
@Setter
public class CarRequestDto {
    @NotNull
    private String model;
    @NotNull
    private String brand;
    @NotNull
    private Car.Type type;
    @NotNull
    @Size(min = 0)
    private int inventory;
    @NotNull
    @Size(min = 0)
    private BigDecimal dailyFee;
}
