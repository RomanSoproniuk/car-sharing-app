package mainpackage.carsharingapp.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mainpackage.carsharingapp.model.Car;

@Getter
@Setter
@ToString
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private Car.Type type;
    private int inventory;
    private BigDecimal dailyFee;

    public CarResponseDto() {
    }

    public CarResponseDto(Long id, String model, String brand, Car.Type type,
                          int inventory, BigDecimal dailyFee) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.inventory = inventory;
        this.dailyFee = dailyFee;
    }
}
