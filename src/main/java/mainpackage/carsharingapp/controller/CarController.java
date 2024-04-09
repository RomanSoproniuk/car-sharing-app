package mainpackage.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/cars")
@RequiredArgsConstructor
@Tag(name = "Car API", description = """
        With the help of this API you will be able to perform certain actions
         with cars and their database, the list of available endpoints and a more
          detailed description will be below
            """)

public class CarController {
    private final CarService carService;

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add car to data base", description = """ 
        By using this endpoint, you will be able to add a car to your database. Please 
        pay attention to certain restrictions, all fields must be filled in, null values 
        are not accepted, when adding the number of cars and the daily fee, please note 
        that their values cannot be less than 0. Equally important, only users with the 
        MANAGER access level have access to this endpoint
            """)
    public CarResponseDto addCar(@RequestBody @Valid CarRequestDto carRequestDto) {
        return carService.addCar(carRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all cars from data base", description = """ 
        With the help of this endpoint you can view all the cars in the catalog,
         please note that this endpoint is public and does not require 
         you to pass authentication
            """)
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carService.getAllCars(pageable);
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    @GetMapping("/{carsId}")
    @Operation(summary = "Get car from data base by id", description = """ 
        Using this endpoint, you can get a specific car using its id. All 
        authenticated users have access to this endpoint. An error will be 
        returned if the id of a car that does not exist is entered
            """)
    public CarResponseDto getCarById(@PathVariable Long carsId) {
        return carService.getCarById(carsId);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{carsId}")
    public CarResponseDto updateCar(@PathVariable Long carsId,
                                    @RequestBody CarRequestDto carRequestDto) {
        return carService.updateCarById(carsId, carRequestDto);
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PatchMapping("/{carsId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update car from data base by id", description = """ 
        With the help of this endpoint, you will be able to update data about 
        the car, please note that only users with MANAGER access can reach this endpoint
            """)
    public void updateCarsInventoryById(
            @PathVariable Long carsId,
             @RequestBody CarsInventoryRequestDto carsInventoryRequestDto) {
        carService.updateCarsInventoryById(carsId, carsInventoryRequestDto);
    }

    @Operation(summary = "Delete car from data base by id", description = """ 
        Using this endpoint, you can delete a specific car by its ID. Please 
        note that when writing this API, soft deletion technology was used, that 
        is, data from the database is not deleted, but only marked as deleted, you 
        can always view them in the database if necessary. I also emphasize that 
        only users with the MANAGER access level have access to this endpoint
            """)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{carsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCarById(@PathVariable Long carsId) {
        carService.deleteCarById(carsId);
    }
}
