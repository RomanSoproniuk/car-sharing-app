package mainpackage.carsharingapp.service;

import java.security.Principal;
import java.util.List;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.dto.ReturnRentalRequestDto;
import mainpackage.carsharingapp.dto.ReturnRentalResponseDto;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto addRental(RentalRequestDto rentalRequestDto);

    ReturnRentalResponseDto addActualReturnDate(
            Long rentalId,
            ReturnRentalRequestDto returnRentalRequestDto);

    RentalResponseDto getRentalByIdForPersonalUser(
            Long rentalId,
            Principal principal);

    List<RentalResponseDto> searchRentalsByParams(
            RentalSearchParameters rentalSearchParameters,
            Pageable pageable);
}
