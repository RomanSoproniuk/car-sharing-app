package mainpackage.carsharingapp.controller;

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.dto.ReturnRentalRequestDto;
import mainpackage.carsharingapp.dto.ReturnRentalResponseDto;
import mainpackage.carsharingapp.service.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/{rentalId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    public RentalResponseDto getRentalByIdForPersonalUser(
            @PathVariable Long rentalId, Principal principal) {
        return rentalService.getRentalByIdForPersonalUser(rentalId, principal);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<RentalResponseDto> searchRentalsByParams(
            RentalSearchParameters rentalSearchParameters, Pageable pageable) {
        return rentalService.searchRentalsByParams(
                rentalSearchParameters, pageable);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public RentalResponseDto addRental(@RequestBody RentalRequestDto rentalRequestDto) {
        return rentalService.addRental(rentalRequestDto);
    }

    @PostMapping("/{rentalId}/return")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ReturnRentalResponseDto addActualReturnDate(
            @PathVariable Long rentalId,
            @RequestBody ReturnRentalRequestDto returnRentalRequestDto) {
        return rentalService.addActualReturnDate(rentalId, returnRentalRequestDto);
    }
}
