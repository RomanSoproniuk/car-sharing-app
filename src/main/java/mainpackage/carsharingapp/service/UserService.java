package mainpackage.carsharingapp.service;

import java.security.Principal;
import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.dto.UserProfileResponseDto;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.dto.UserUpdateProfileRequestDto;
import mainpackage.carsharingapp.exceptions.RegistrationException;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    UserResponseDto updateUserRole(RoleRequestDto roleRequestDto, Long id);

    UserProfileResponseDto getProfileInfo(Principal principal);

    UserProfileResponseDto updateProfileInfo(
            Principal principal,
            UserUpdateProfileRequestDto userUpdateProfileRequestDto);
}
