package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponseDto {
    private String email;
    private String firstName;
    private String lastName;
    private String message;

    public UserProfileResponseDto() {
    }

    public UserProfileResponseDto(String email, String firstName, String lastName, String message) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.message = message;
    }
}
