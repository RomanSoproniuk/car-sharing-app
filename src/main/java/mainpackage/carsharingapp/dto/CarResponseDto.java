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
}
