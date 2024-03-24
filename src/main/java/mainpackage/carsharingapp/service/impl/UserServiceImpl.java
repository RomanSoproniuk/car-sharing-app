package mainpackage.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.exceptions.RegistrationException;
import mainpackage.carsharingapp.exceptions.RoleException;
import mainpackage.carsharingapp.mapper.RoleMapper;
import mainpackage.carsharingapp.mapper.UserMapper;
import mainpackage.carsharingapp.model.Role;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleMapper roleMapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(userRegistrationRequestDto.getEmail()).isPresent()) {
            throw new RegistrationException("You can not register by email: "
                    + userRegistrationRequestDto.getEmail()
                    + " since user with same email is exist");
        }
        User user = new User();
        user.setEmail(userRegistrationRequestDto.getEmail());
        user.setFirstName(userRegistrationRequestDto.getFirstName());
        user.setLastName(userRegistrationRequestDto.getLastName());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDto.getPassword()));
        Role userRole = new Role();
        userRole.setName(Role.RoleName.USER);
        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public void updateUserRole(RoleRequestDto roleRequestDto, Long id) {
        Role roleForUpdate = roleMapper.toEntity(roleRequestDto);
        User userFromDbById = userRepository.findAllById(id).orElseThrow(() ->
                new EntityNotFoundException("User by id: " + id
                        + " does not exist in DB"));
        Set<Role> userRoles = userFromDbById.getRoles();
        if (userRoles.contains(roleForUpdate)) {
            throw new RoleException("User by id: " + id
                    + " already has role " + roleRequestDto.getRoleName());
        }
        userRoles.add(roleForUpdate);
        userFromDbById.setRoles(userRoles);
        userRepository.save(userFromDbById);
    }
}
