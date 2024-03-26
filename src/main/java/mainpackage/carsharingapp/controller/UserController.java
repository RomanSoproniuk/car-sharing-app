package mainpackage.carsharingapp.controller;

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
public class UserController {
    private final UserService userService;

    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void updateUserRole(@RequestBody RoleRequestDto roleRequestDto,
                               @PathVariable Long id) {
        userService.updateUserRole(roleRequestDto, id);
    }

    @GetMapping("/me")
    public UserProfileResponseDto getProfileInfo(Principal principal) {
        return userService.getProfileInfo(principal);
    }

    @PutMapping("/me")
    public UserProfileResponseDto updateProfileInfo(
            Principal principal,
            @RequestBody UserUpdateProfileRequestDto userUpdateProfileRequestDto) {
        return userService.updateProfileInfo(principal, userUpdateProfileRequestDto);
    }
}
