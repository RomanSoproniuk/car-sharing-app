package mainpackage.carsharingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @Min(0)
    private int inventory;
    @NotNull
    private BigDecimal dailyFee;

    public CarRequestDto() {
    }

    public CarRequestDto(String model, String brand, Car.Type type,
                         Integer inventory, BigDecimal dailyFee) {
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.inventory = inventory;
        this.dailyFee = dailyFee;
    }
}
