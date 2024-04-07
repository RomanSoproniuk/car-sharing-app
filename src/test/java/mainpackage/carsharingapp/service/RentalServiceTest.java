package mainpackage.carsharingapp.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.dto.ReturnRentalRequestDto;
import mainpackage.carsharingapp.dto.ReturnRentalResponseDto;
import mainpackage.carsharingapp.mapper.impl.CarMapperImpl;
import mainpackage.carsharingapp.mapper.impl.RentalMapperImpl;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.RentalSpecificationBuilder;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.impl.RentalServiceImpl;
import mainpackage.carsharingapp.service.impl.TelegramNotificationService;
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
import org.springframework.data.jpa.domain.Specification;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    private final Pageable pageable = PageRequest.of(0, 5);
    @InjectMocks
    private RentalServiceImpl rentalService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapperImpl rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarMapperImpl carMapper;
    @Mock
    private TelegramNotificationService telegramNotificationService;
    @Mock
    private RentalSpecificationBuilder rentalSpecificationBuilder;
    private Principal principal;
    private RentalRequestDto rentalRequestDto;
    private RentalResponseDto rentalResponseDto;
    private RentalResponseDto rentalResponseDtoWithActualReturnDate;
    private CarResponseDto carResponseDto;
    private Rental rental;
    private Rental rentalWithActualReturnDate;
    private Long carId;
    private Long rentalId;
    private Car car;
    private Car savedCarAfterCreateRental;
    private Car savedCarAfterAddActualReturnDate;
    private LocalDate actualReturnDate;
    private ReturnRentalRequestDto returnRentalRequestDto;
    private ReturnRentalResponseDto returnRentalResponseDto;
    private RentalSearchParameters rentalSearchParameters;
    private User user;
    private Page<Rental> rentalPage;
    private Specification<Rental> rentalSpecification;

    @BeforeEach
    public void setUp() {
        user = new User(2L, "bob@gmail.com", "Bob", "Alison",
                "12345678", false);
        carResponseDto = new CarResponseDto(1L, "Q8", "Audi",
                Car.Type.SEDAN, 5, BigDecimal.valueOf(20L));
        rentalResponseDto = new RentalResponseDto(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), null, 1L,
                2L,carResponseDto);
        savedCarAfterCreateRental = new Car(1L, "Q8", "Audi", Car.Type.SEDAN,
                4, BigDecimal.valueOf(20L), false);
        savedCarAfterAddActualReturnDate = new Car(1L, "Q8", "Audi", Car.Type.SEDAN,
                6, BigDecimal.valueOf(20L), false);
        car = new Car(1L, "Q8", "Audi", Car.Type.SEDAN,
                5, BigDecimal.valueOf(20L), false);
        carId = 1L;
        rentalId = 1L;
        actualReturnDate = LocalDate.of(2024, 5, 20);
        returnRentalRequestDto = new ReturnRentalRequestDto(actualReturnDate);
        rentalResponseDtoWithActualReturnDate = new RentalResponseDto(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), actualReturnDate, 1L,
                2L,carResponseDto);
        rentalRequestDto = new RentalRequestDto(
                LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20),
                1L, 2L);
        rental = new Rental(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), null, 1L, 2L,
                false, true);
        rentalWithActualReturnDate = new Rental(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                1L, 2L, false, true);
        returnRentalResponseDto = new ReturnRentalResponseDto(actualReturnDate);
        rentalSearchParameters = new RentalSearchParameters(new Boolean[]{true},
                new Long[]{1L});
        rentalPage = new PageImpl<>(List.of(rental), pageable, 5);
        principal = new Principal() {
            @Override
            public String getName() {
                return "bob@gmail.com";
            }
        };
        rentalSpecification = new Specification<Rental>() {
            @Override
            public Predicate toPredicate(Root<Rental> root, CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };
    }

    @Test
    @DisplayName("""
            Add new rental to DB
            """)
    public void addRental_AddNewRentalToDb_Success() throws TelegramApiException {
        //given
        Mockito.when(rentalMapper.toEntity(rentalRequestDto)).thenReturn(rental);
        Mockito.when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        Mockito.when(carRepository.save(Mockito.any())).thenReturn(savedCarAfterCreateRental);
        Mockito.when(rentalRepository.save(Mockito.any())).thenReturn(rental);
        Mockito.when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);
        Mockito.when(carMapper.toDto(Mockito.any())).thenReturn(carResponseDto);
        Mockito.doNothing().when(telegramNotificationService)
                .sendMessageNewRentalCreation(rentalResponseDto);
        RentalResponseDto expected = rentalResponseDto;
        //when
        RentalResponseDto actual = rentalService.addRental(rentalRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Test correct add actual return date
            """)
    public void addActualReturnDate_CorrectAddActualReturnDate_Success() {
        //given
        Mockito.when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        Mockito.when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        Mockito.when(carRepository.save(Mockito.any()))
                .thenReturn(savedCarAfterAddActualReturnDate);
        Mockito.when(rentalRepository.save(Mockito.any())).thenReturn(rentalWithActualReturnDate);
        Mockito.when(rentalMapper.toReturnRentalResponseRentalDto(Mockito.any()))
                .thenReturn(returnRentalResponseDto);
        ReturnRentalResponseDto expected = returnRentalResponseDto;
        //when
        ReturnRentalResponseDto actual
                = rentalService.addActualReturnDate(rentalId, returnRentalRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Get correct rental by personal user
            """)
    public void getRentalByIdForPersonalUser_ReturnCorrectRental_Success() {
        //given
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(rentalRepository.findById(Mockito.any())).thenReturn(Optional.of(rental));
        Mockito.when(rentalMapper.toDto(Mockito.any())).thenReturn(rentalResponseDto);
        Mockito.when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        Mockito.when(carMapper.toDto(Mockito.any())).thenReturn(carResponseDto);
        RentalResponseDto expected = rentalResponseDto;
        //when
        RentalResponseDto actual = rentalService.getRentalByIdForPersonalUser(1L, principal);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Search rentals by parameter
            """)
    public void searchRentalsByParams_ReturnCorrectRental_Success() {
        //given
        Mockito.when(rentalSpecificationBuilder.build(rentalSearchParameters))
                .thenReturn(rentalSpecification);
        Mockito.when(rentalRepository.findAll(rentalSpecification, pageable))
                .thenReturn(rentalPage);
        Mockito.when(rentalMapper.toDto(rental)).thenReturn(rentalResponseDto);
        List<RentalResponseDto> expected = List.of(rentalResponseDto);
        //when
        List<RentalResponseDto> actual =
                rentalService.searchRentalsByParams(rentalSearchParameters, pageable);
        //then
        Assertions.assertEquals(expected.size(), actual.size());
        EqualsBuilder.reflectionEquals(expected.get(0), actual.get(0));
    }
}
