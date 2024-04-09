package mainpackage.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Rental API", description = """
        With the help of this API, you can work with the lease,
         and perform certain operations, which are presented below
            """)

public class RentalController {
    private final RentalService rentalService;

    @GetMapping("/{rentalId}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER') or hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get a rental by user ID", description = """ 
        With this endpoint you should be able to access the rental using 
        an ID, unauthenticated users cannot access this endpoint
            """)
    public RentalResponseDto getRentalByIdForPersonalUser(
            @PathVariable Long rentalId, Principal principal) {
        return rentalService.getRentalByIdForPersonalUser(rentalId, principal);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Search rental by params", description = """ 
        This API allows you to search for rentals using certain parameters. 
        I would like to clarify that users with the MANAGER level and 
        above have access to this endpoint
            """)
    public List<RentalResponseDto> searchRentalsByParams(
            RentalSearchParameters rentalSearchParameters, Pageable pageable) {
        return rentalService.searchRentalsByParams(
                rentalSearchParameters, pageable);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(value = HttpStatus.OK)
    @Operation(summary = "Add new rental", description = """ 
    With the help of this endpoint, you can add new leases to the database. 
    Only users with MANAGER status and above have access
            """)
    public RentalResponseDto addRental(@RequestBody RentalRequestDto rentalRequestDto) {
        return rentalService.addRental(rentalRequestDto);
    }

    @PostMapping("/{rentalId}/return")
    @ResponseStatus(value = HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Add actual return date", description = """ 
    With the help of this endpoint, after the user returns the car, 
    you can set the current date of return to the rental, only authenticated 
    users with the status of MANAGER and above have access to this endpoint
            """)
    public ReturnRentalResponseDto addActualReturnDate(
            @PathVariable Long rentalId,
            @RequestBody ReturnRentalRequestDto returnRentalRequestDto) {
        return rentalService.addActualReturnDate(rentalId, returnRentalRequestDto);
    }
}
