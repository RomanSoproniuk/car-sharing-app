package mainpackage.carsharingapp;

import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cars")
public class CarController {

    @GetMapping
    public CarResponseDto getAllCars(@RequestBody CarRequestDto carRequestDto,
                                     Pageable pageable) {

    }
}
