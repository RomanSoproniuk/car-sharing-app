package mainpackage.carsharingapp.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import mainpackage.carsharingapp.model.Car;

@Getter
@Setter
public class CarRequestDto {
    private String model;
    private String brand;
    private Car.Type type;
    private int inventory;
    private BigDecimal dailyFee;
}
