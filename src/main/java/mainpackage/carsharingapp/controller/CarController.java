package mainpackage.carsharingapp.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.CarsInventoryRequestDto;
import mainpackage.carsharingapp.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto addCar(@RequestBody CarRequestDto carRequestDto) {
        return carService.addCar(carRequestDto);
    }

    @GetMapping
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carService.getAllCars(pageable);
    }

    @GetMapping("/{carsId}")
    public CarResponseDto getCarById(@PathVariable Long carsId) {
        return carService.getCarById(carsId);
    }

    @PutMapping("/{carsId}")
    public CarResponseDto updateCar(@PathVariable Long carsId,
                                    @RequestBody CarRequestDto carRequestDto) {
        return carService.updateCarById(carsId, carRequestDto);
    }

    @PatchMapping("/{carsId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCarsInventoryById(
            @PathVariable Long carsId,
             @RequestBody CarsInventoryRequestDto carsInventoryRequestDto) {
        carService.updateCarsInventoryById(carsId, carsInventoryRequestDto);
    }

    @DeleteMapping("/{carsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCarById(@PathVariable Long carsId) {
        carService.deleteCarById(carsId);
    }
}
