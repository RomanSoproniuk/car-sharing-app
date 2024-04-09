package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarsInventoryRequestDto {
    private int inventory;

    public CarsInventoryRequestDto() {
    }

    public CarsInventoryRequestDto(int inventory) {
        this.inventory = inventory;
    }
}
