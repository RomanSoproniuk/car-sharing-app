package mainpackage.carsharingapp.service;

import java.security.Principal;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.ReturnRentalRequestDto;
import mainpackage.carsharingapp.dto.ReturnRentalResponseDto;

public interface RentalService {
    RentalResponseDto addRental(RentalRequestDto rentalRequestDto);

    ReturnRentalResponseDto addActualReturnDate(
            Long rentalId,
            ReturnRentalRequestDto returnRentalRequestDto);

    RentalResponseDto getRentalByIdForPersonalUser(
            Long rentalId,
            Principal principal);
}
