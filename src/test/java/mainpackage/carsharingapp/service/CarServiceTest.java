package mainpackage.carsharingapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.CarsInventoryRequestDto;
import mainpackage.carsharingapp.mapper.impl.CarMapperImpl;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.service.impl.CarServiceImpl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapperImpl carMapper;
    @InjectMocks
    private CarServiceImpl carService;
    private final Pageable pageable = PageRequest.of(0, 5);
    private Car firstCar;
    private Car secondCar;
    private Car updatedCar;
    private CarResponseDto firstCarResponseDto;
    private CarResponseDto secondCarResponseDto;
    private CarResponseDto updatedCarResponseDto;
    private CarRequestDto firstCarRequestDto;
    private CarRequestDto secondCarRequestDto;
    private Long firstCarId;
    private Long secondCarId;
    private Page<Car> carPage;

    @BeforeEach
    public void setUp() {
        firstCarId = 1L;
        secondCarId = 2L;
        firstCar = new Car(1L, "Q8", "Audi", Car.Type.SEDAN,
                5, BigDecimal.valueOf(20L), false);
        secondCar = new Car(2L, "X5", "BMW", Car.Type.HATCHBACK,
                1, BigDecimal.valueOf(30L), false);
        updatedCar = new Car(1L, "X5", "BMW", Car.Type.HATCHBACK,
                1, BigDecimal.valueOf(30L), false);
        firstCarResponseDto = new CarResponseDto(1L, "Q8", "Audi",
                Car.Type.SEDAN, 5, BigDecimal.valueOf(20L));
        secondCarResponseDto = new CarResponseDto(2L, "X5", "BMW",
                Car.Type.HATCHBACK, 1, BigDecimal.valueOf(30L));
        updatedCarResponseDto = new CarResponseDto(2L, "X5", "BMW",
                Car.Type.HATCHBACK, 1, BigDecimal.valueOf(30L));
        firstCarRequestDto = new CarRequestDto("Q8", "Audi", Car.Type.SEDAN,
                5, BigDecimal.valueOf(20L));
        secondCarRequestDto = new CarRequestDto("X5", "BMW", Car.Type.HATCHBACK,
                1, BigDecimal.valueOf(30L));
        carPage = new PageImpl<>(List.of(firstCar, secondCar), pageable, 5);
    }

    @Test
    @DisplayName("""
            Return all cars from DB
            """)
    public void getAllCars_ReturnAllCarsFromDb_Success() {
        //given
        Mockito.when(carRepository.findAll(pageable)).thenReturn(carPage);
        Mockito.when(carMapper.toDto(firstCar)).thenReturn(firstCarResponseDto);
        Mockito.when(carMapper.toDto(secondCar)).thenReturn(secondCarResponseDto);
        List<CarResponseDto> expected = List.of(firstCarResponseDto, secondCarResponseDto);
        //when
        List<CarResponseDto> actual = carService.getAllCars(pageable);
        //then
        Assertions.assertEquals(expected.size(), actual.size());
        EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0));
        EqualsBuilder.reflectionEquals(expected.get(1), actual.get(1));
    }

    @Test
    @DisplayName("""
            Add car to DB
            """)
    public void addCar_AddNewCarToDb_Success() {
        //given
        Mockito.when(carMapper.toEntity(firstCarRequestDto)).thenReturn(firstCar);
        Mockito.when(carRepository.save(Mockito.any())).thenReturn(firstCar);
        Mockito.when(carMapper.toDto(firstCar)).thenReturn(firstCarResponseDto);
        CarResponseDto expected = firstCarResponseDto;
        //when
        CarResponseDto actual = carService.addCar(firstCarRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Return car by id
            """)
    public void getCarById_ReturnCarById_Success() {
        //given
        Mockito.when(carRepository.findById(secondCarId)).thenReturn(Optional.of(secondCar));
        Mockito.when(carMapper.toDto(secondCar)).thenReturn(secondCarResponseDto);
        CarResponseDto expected = secondCarResponseDto;
        //when
        CarResponseDto actual = carService.getCarById(secondCarId);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Correct upadate car by id
            """)
    public void updateCarById_CorrectUpdateCarById() {
        //given
        Mockito.when(carRepository.findById(firstCarId)).thenReturn(Optional.of(firstCar));
        Mockito.when(carMapper.toEntity(secondCarRequestDto)).thenReturn(secondCar);
        Mockito.when(carRepository.save(Mockito.any())).thenReturn(updatedCar);
        Mockito.when(carMapper.toDto(updatedCar)).thenReturn(updatedCarResponseDto);
        CarResponseDto expected = updatedCarResponseDto;
        //when
        CarResponseDto actual = carService.updateCarById(firstCarId, secondCarRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Test correct update inventory by car id
            """)
    public void updateCarsInventoryById_CorrectUpdateCarInventoryById_Success() {
        //given
        Mockito.when(carRepository.findById(firstCarId)).thenReturn(Optional.of(firstCar));
        Mockito.when(carRepository.save(firstCar)).thenReturn(firstCar);
        //when
        carService.updateCarsInventoryById(firstCarId, new CarsInventoryRequestDto(20));
    }
}
