package mainpackage.carsharingapp.service;

import java.security.Principal;
import java.util.Optional;
import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.dto.UserProfileResponseDto;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.dto.UserUpdateProfileRequestDto;
import mainpackage.carsharingapp.exceptions.RegistrationException;
import mainpackage.carsharingapp.mapper.RoleMapper;
import mainpackage.carsharingapp.mapper.impl.UserMapperImpl;
import mainpackage.carsharingapp.model.Role;
import mainpackage.carsharingapp.model.User;
import mainpackage.carsharingapp.repository.RoleRepository;
import mainpackage.carsharingapp.repository.UserRepository;
import mainpackage.carsharingapp.service.impl.UserServiceImpl;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapperImpl userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("""
            Validate how work register user method            
            """)
    public void registerUser_ReturnRegisteredUser_Success()
            throws RegistrationException {
        //given
        UserRegistrationRequestDto userRegistrationRequestDto
                = new UserRegistrationRequestDto(
                "bob@gmail.com",
                "Bob",
                "Alison",
                "12345678",
                "12345678");
        Role userRole = new Role();
        userRole.setName(Role.RoleName.CUSTOMER);
        userRole.setId(2L);
        User savedUser = new User(
                2L,
                "bob@gmail.com",
                "Bob",
                "Alison",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy",
                false);
        savedUser.getRoles().add(userRole);
        UserResponseDto expectedResponseDto = new UserResponseDto(2L, "bob@gmail.com");
        Mockito.when(roleRepository.findById(2L)).thenReturn(Optional.of(userRole));
        Mockito.when(passwordEncoder.encode("12345678"))
                .thenReturn("$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy");
        Mockito.when(userMapper.toUserResponse(savedUser)).thenReturn(expectedResponseDto);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);
        //when
        UserResponseDto actualResponseDto
                = userService.registerUser(userRegistrationRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("""
            Correct update roles user           
            """)
    public void updateUserRole_CorrectUpdateRoleUser_Success() {
        //given
        RoleRequestDto roleRequestDto = new RoleRequestDto("MANAGER");
        Role roleForUpdate = new Role(1L, Role.RoleName.MANAGER);
        Long userId = 2L;
        User savedUser = new User(
                2L,
                "bob@gmail.com",
                "Bob",
                "Alison",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy",
                false);
        savedUser.getRoles().add(new Role(2L, Role.RoleName.CUSTOMER));
        UserResponseDto expected = new UserResponseDto(2L, "bob@gmail.com");
        Mockito.when(roleMapper.toEntity(roleRequestDto)).thenReturn(roleForUpdate);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        Mockito.when(roleRepository.findByName(roleForUpdate.getName()))
                .thenReturn(Optional.of(roleForUpdate));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(savedUser);
        Mockito.when(userMapper.toUserResponse(savedUser)).thenReturn(expected);
        //when
        UserResponseDto actual = userService.updateUserRole(roleRequestDto, userId);
        System.out.println();
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Correct update roles user           
            """)
    public void getProfileInfo_ReturnCorrectProfileInfo_Success() {
        //given
        User userFormDb = new User(
                2L,
                "bob@gmail.com",
                "Bob",
                "Alison",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy",
                false);
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "bob@gmail.com";
            }
        };
        UserProfileResponseDto expected = new UserProfileResponseDto(
                "bob@gmail.com",
                "Bob",
                "Alison",
                null);
        Mockito.when(userRepository.findByEmail("bob@gmail.com")).thenReturn(Optional.of(userFormDb));
        Mockito.when(userMapper.toUserProfileResponse(userFormDb)).thenReturn(expected);
        //when
        UserProfileResponseDto actual = userService.getProfileInfo(principal);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Update profile info change test          
            """)
    public void updateProfileInfo_UpdateCorrectProfileInfo_Success() {
        //given
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return "bob@gmail.com";
            }
        };
        User userFormDb = new User(
                2L,
                "bob@gmail.com",
                "Bob",
                "Alison",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy",
                false);
        UserUpdateProfileRequestDto userUpdateProfileRequestDto
                = new UserUpdateProfileRequestDto(
                "alice@gmail.com",
                "Alice",
                "Bobson");
        User updatedUser = new User(
                2L,
                "alice@gmail.com",
                "Alice",
                "Bobson",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy",
                false);
        UserProfileResponseDto expected = new UserProfileResponseDto(
                "alice@gmail.com",
                "Alice",
                "Bobson",
                "If you changed your email, please log in with new email"
        );
        Mockito.when(userRepository.findByEmail("bob@gmail.com")).thenReturn(Optional.of(userFormDb));
        Mockito.when(userRepository.findByEmail("alice@gmail.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(updatedUser);
        Mockito.when(userMapper.toUserProfileResponse(updatedUser)).thenReturn(expected);
        //when
        UserProfileResponseDto actual
                = userService.updateProfileInfo(principal, userUpdateProfileRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

}
