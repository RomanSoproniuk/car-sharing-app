package mainpackage.carsharingapp.service;

import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.exceptions.RegistrationException;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    void updateUserRole(RoleRequestDto roleRequestDto, Long id);
}
