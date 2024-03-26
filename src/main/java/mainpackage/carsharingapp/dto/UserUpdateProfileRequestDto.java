package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateProfileRequestDto {
    private String email;
    private String firstName;
    private String lastName;
}
