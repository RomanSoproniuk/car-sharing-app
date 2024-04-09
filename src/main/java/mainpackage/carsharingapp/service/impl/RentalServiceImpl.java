package mainpackage.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.dto.ReturnRentalRequestDto;
import mainpackage.carsharingapp.dto.ReturnRentalResponseDto;
import mainpackage.carsharingapp.exceptions.AccessException;
import mainpackage.carsharingapp.exceptions.CarException;
import mainpackage.carsharingapp.exceptions.RentalException;
import mainpackage.carsharingapp.mapper.CarMapper;
import mainpackage.carsharingapp.mapper.RentalMapper;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.CarRepository;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.RentalSpecificationBuilder;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private static final int DECREASE_BY_ONE = 1;
    private static final int INCREASE_BY_ONE = 1;
    private static final boolean STATUS_AFTER_RETURNING_CAR = false;
    private static final int MINIMAL_NUMBERS_OF_CARS = 0;
    private final RentalSpecificationBuilder rentalSpecificationBuilder;
    private final RentalMapper rentalMapper;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarMapper carMapper;
    private final TelegramNotificationService telegramNotificationService;

    @Override
    public RentalResponseDto addRental(RentalRequestDto rentalRequestDto) {
        Rental newRental = rentalMapper.toEntity(rentalRequestDto);
        Long carIdForUpdate = newRental.getCarId();
        Car carFromDb = carRepository.findById(carIdForUpdate).orElseThrow(() ->
                new EntityNotFoundException("Car by id " + carIdForUpdate
                        + " does not exist in DB"));
        int currentNumbersOfCar = carFromDb.getInventory();
        if (currentNumbersOfCar <= MINIMAL_NUMBERS_OF_CARS) {
            throw new CarException("User can not rent car, since they are not available");
        }
        int numbersCarAfterUpdate = currentNumbersOfCar - DECREASE_BY_ONE;
        carFromDb.setInventory(numbersCarAfterUpdate);
        Car savedCar = carRepository.save(carFromDb);
        Rental savedRental = rentalRepository.save(newRental);
        RentalResponseDto rentalResponseDto = rentalMapper.toDto(savedRental);
        rentalResponseDto.setCarResponseDto(carMapper.toDto(savedCar));
        try {
            telegramNotificationService.sendMessageNewRentalCreation(rentalResponseDto);
        } catch (TelegramApiException e) {
            throw new RentalException("Cant send notification to Telegram chat about new rental");
        }
        return rentalResponseDto;
    }

    @Override
    public ReturnRentalResponseDto addActualReturnDate(
            Long rentalId,
            ReturnRentalRequestDto returnRentalRequestDto) {
        Rental currentRental = rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Rental by id " + rentalId
                        + " does not exist in DB"));
        Long carId = currentRental.getCarId();
        Car rentalCar = carRepository.findById(carId).get();
        if (currentRental.getActualReturnDate() != null) {
            throw new RentalException("You can not add actual return date twice. Rental by id "
                + currentRental.getId() + " has the actual return date");
        }
        int currentNumbersOfCar = rentalCar.getInventory();
        int numbersCarAfterUpdate = currentNumbersOfCar + INCREASE_BY_ONE;
        rentalCar.setInventory(numbersCarAfterUpdate);
        carRepository.save(rentalCar);
        LocalDate actualReturnDate = returnRentalRequestDto.getActualReturnDate();
        currentRental.setActualReturnDate(actualReturnDate);
        currentRental.setActive(STATUS_AFTER_RETURNING_CAR);
        Rental updateRental = rentalRepository.save(currentRental);
        return rentalMapper.toReturnRentalResponseRentalDto(updateRental);
    }

    @Override
    public RentalResponseDto getRentalByIdForPersonalUser(Long rentalId, Principal principal) {
        String userEmail = principal.getName();
        User userFromDbByEmail = userRepository.findByEmail(userEmail).get();
        Long currentUserId = userFromDbByEmail.getId();
        Rental rentalFromDbById = rentalRepository.findById(rentalId).orElseThrow(() ->
                new EntityNotFoundException("Rental by id " + rentalId
                        + " does not exist in DB"));
        if (!Objects.equals(currentUserId, rentalFromDbById.getUserId())) {
            throw new AccessException("You do not have sufficient rights to view data on "
                    + "this rental");
        }
        RentalResponseDto rentalResponseDto = rentalMapper.toDto(rentalFromDbById);
        Car carFromDbByIdFromRental = carRepository.findById(rentalFromDbById.getCarId()).get();
        rentalResponseDto.setCarResponseDto(carMapper.toDto(carFromDbByIdFromRental));
        return rentalResponseDto;
    }

    @Override
    public List<RentalResponseDto> searchRentalsByParams(
            RentalSearchParameters rentalSearchParameters,
            Pageable pageable) {
        Specification<Rental> rentalSpecification
                = rentalSpecificationBuilder.build(rentalSearchParameters);
        return rentalRepository.findAll(rentalSpecification, pageable).stream()
                .map(rentalMapper::toDto)
                .toList();
    }
}
