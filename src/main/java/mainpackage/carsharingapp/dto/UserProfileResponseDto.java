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
}
