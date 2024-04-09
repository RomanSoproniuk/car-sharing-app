package mainpackage.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.UserLoginRequestDto;
import mainpackage.carsharingapp.dto.UserLoginResponseDto;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.exceptions.RegistrationException;
import mainpackage.carsharingapp.security.AuthenticationService;
import mainpackage.carsharingapp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = """
        This API is designed to perform authentication and registration processes.
         Access to the endpoints of this controller is public, which allows you to 
         continue working with the application after registration and authentication
            """)

public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Login endpoint", description = """ 
        After the user has registered, he can log in to the program using his credentials,
         namely, using the email and password he used when registering in the application.
          After the authentication process, a JWT token will be returned in response for
           further cooperation with the application, because the application is stateless,
            to understand how to work with the token returned to you in response, please refer
             to the official documentation
            """)

    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        return authenticationService.authenticate(userLoginRequestDto);
    }

    @PostMapping("/registration")
    @Operation(summary = "Registration endpoint", description = """ 
        When registering, the user enters his personal data, please note that the email must 
        be in standard format, because it is verified, and the passwords he enters during 
        registration must match
            """)
    public UserResponseDto registerUser(@RequestBody
                                        @Valid
                                        UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException {
        return userService.registerUser(userRegistrationRequestDto);
    }
}
