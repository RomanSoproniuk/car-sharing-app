package mainpackage.carsharingapp.service;

import java.util.List;
import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.CarsInventoryRequestDto;
import org.springframework.data.domain.Pageable;

public interface CarService {
    List<CarResponseDto> getAllCars(Pageable pageable);

    CarResponseDto addCar(CarRequestDto carRequestDto);

    CarResponseDto getCarById(Long carsId);

    CarResponseDto updateCarById(Long carsId, CarRequestDto carRequestDto);

    void updateCarsInventoryById(Long carsId, CarsInventoryRequestDto carsInventoryRequestDto);

    void deleteCarById(Long carsId);
}
