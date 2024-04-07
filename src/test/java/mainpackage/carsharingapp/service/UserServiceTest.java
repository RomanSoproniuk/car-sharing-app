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
import org.junit.jupiter.api.BeforeEach;
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
    private UserRegistrationRequestDto userRegistrationRequestDto;
    private Role userRole;
    private Role roleForUpdate;
    private User user;
    private UserResponseDto userResponseDto;
    private Long userId;
    private String rawPassword;
    private String encodedPassword;
    private RoleRequestDto roleRequestDto;
    private Principal principal;
    private UserProfileResponseDto userProfileResponseDto;
    private UserUpdateProfileRequestDto userUpdateProfileRequestDto;
    private User updatedUser;
    private UserProfileResponseDto userUpdatedProfileResponseDto;

    @BeforeEach
    public void setUp() {
        roleRequestDto = new RoleRequestDto("MANAGER");
        rawPassword = "12345678";
        encodedPassword = "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy";
        userId = 2L;
        userRegistrationRequestDto = new UserRegistrationRequestDto("bob@gmail.com",
                "Bob", "Alison", "12345678", "12345678");
        userRole = new Role(2L, Role.RoleName.CUSTOMER);
        roleForUpdate = new Role(1L, Role.RoleName.MANAGER);
        user = new User(2L, "bob@gmail.com", "Bob", "Alison",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy", false);
        user.getRoles().add(userRole);
        userResponseDto = new UserResponseDto(2L, "bob@gmail.com");
        userProfileResponseDto = new UserProfileResponseDto("bob@gmail.com", "Bob", "Alison", null);
        userUpdateProfileRequestDto = new UserUpdateProfileRequestDto("alice@gmail.com", "Alice",
                "Bobson");
        updatedUser = new User(2L, "alice@gmail.com", "Alice", "Bobson",
                "$2a$10$aSRC5P15RuyGCkIUQSvV7espH9sESixV/jDpsQomHruZlzNRck0Fy", false);
        userUpdatedProfileResponseDto = new UserProfileResponseDto("alice@gmail.com", "Alice",
                "Bobson", "If you changed your email, please log in with new email"
        );
        principal = new Principal() {
            @Override
            public String getName() {
                return "bob@gmail.com";
            }
        };
    }

    @Test
    @DisplayName("""
            Validate how work register user method            
            """)
    public void registerUser_ReturnRegisteredUser_Success()
            throws RegistrationException {
        //given
        UserResponseDto expectedResponseDto = userResponseDto;
        Mockito.when(roleRepository.findById(userId)).thenReturn(Optional.of(userRole));
        Mockito.when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        Mockito.when(userMapper.toUserResponse(user)).thenReturn(expectedResponseDto);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
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
        UserResponseDto expected = userResponseDto;
        Mockito.when(roleMapper.toEntity(roleRequestDto)).thenReturn(roleForUpdate);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(roleRepository.findByName(roleForUpdate.getName()))
                .thenReturn(Optional.of(roleForUpdate));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(userMapper.toUserResponse(user)).thenReturn(expected);
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
        UserProfileResponseDto expected = userProfileResponseDto;
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(userMapper.toUserProfileResponse(user)).thenReturn(expected);
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
        UserProfileResponseDto expected = userUpdatedProfileResponseDto;
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(updatedUser.getEmail()))
                .thenReturn(Optional.empty());
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(updatedUser);
        Mockito.when(userMapper.toUserProfileResponse(updatedUser)).thenReturn(expected);
        //when
        UserProfileResponseDto actual
                = userService.updateProfileInfo(principal, userUpdateProfileRequestDto);
        //then
        EqualsBuilder.reflectionEquals(expected, actual);
    }

}
