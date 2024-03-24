package mainpackage.carsharingapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank
        @Email
        @Size(min = 2, max = 20)
        String email,
        @NotBlank
        @Size(min = 2, max = 20)
        String password
) {
}
