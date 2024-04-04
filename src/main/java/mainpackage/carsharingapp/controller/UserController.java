package mainpackage.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.dto.UserProfileResponseDto;
import mainpackage.carsharingapp.dto.UserUpdateProfileRequestDto;
import mainpackage.carsharingapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User API", description = """
With the help of this controller, you can perform certain actions 
with the user, you can read more about it below
        """)
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update users roles", description = """ 
        When using this API, you can change the access level for this user, 
        it is worth noting that only users with the MANAGER access level 
        can use this endpoint
            """)
    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void updateUserRole(@RequestBody RoleRequestDto roleRequestDto,
                               @PathVariable Long id) {
        userService.updateUserRole(roleRequestDto, id);
    }

    @GetMapping("/me")
    @Operation(summary = "Get profile info", description = """ 
        With the help of this endpoint, you can get detailed 
        information about the user, namely his personal data
            """)
    public UserProfileResponseDto getProfileInfo(Principal principal) {
        return userService.getProfileInfo(principal);
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile info", description = """ 
        Using this endpoint, the user can change his personal data
            """)
    public UserProfileResponseDto updateProfileInfo(
            Principal principal,
            @RequestBody UserUpdateProfileRequestDto userUpdateProfileRequestDto) {
        return userService.updateProfileInfo(principal, userUpdateProfileRequestDto);
    }
}
