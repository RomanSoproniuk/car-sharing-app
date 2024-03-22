package mainpackage.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.CarsInventoryRequestDto;
import mainpackage.carsharingapp.mapper.CarMapper;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public List<CarResponseDto> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarResponseDto addCar(CarRequestDto carRequestDto) {
        return carMapper.toDto(carRepository.save(carMapper.toEntity(carRequestDto)));
    }

    @Override
    public CarResponseDto getCarById(Long carsId) {
        Car carByIdFromDb = carRepository.findById(carsId)
                .orElseThrow(() ->
                        new EntityNotFoundException("The car with id "
                                + carsId + " currently does not exist in the database"));
        return carMapper.toDto(carByIdFromDb);
    }

    @Override
    public CarResponseDto updateCarById(Long carsId, CarRequestDto carRequestDto) {
        checkAvailabilityCarById(carsId);
        Car carForUpdate = carMapper.toEntity(carRequestDto);
        carForUpdate.setId(carsId);
        return carMapper.toDto(carRepository.save(carForUpdate));
    }

    @Override
    public void updateCarsInventoryById(Long carsId,
                                        CarsInventoryRequestDto carsInventoryRequestDto) {
        Car carByIdFromDb = carRepository.findById(carsId).orElseThrow(() ->
                new EntityNotFoundException("You can't update car inventory by id" + carsId
                        + "since car by this id doesn't exist in DB"));
        carByIdFromDb.setInventory(carsInventoryRequestDto.getInventory());
        carRepository.save(carByIdFromDb);
    }

    @Override
    public void deleteCarById(Long carsId) {
        checkAvailabilityCarById(carsId);
        carRepository.deleteById(carsId);
    }

    private void checkAvailabilityCarById(Long carsId) {
        if (carRepository.findById(carsId).isEmpty()) {
            throw new EntityNotFoundException("You can't manage car by id" + carsId
                    + "since car by this id doesn't exist in DB");
        }
    }
}
