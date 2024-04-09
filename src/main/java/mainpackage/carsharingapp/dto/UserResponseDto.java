package mainpackage.carsharingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String email) {
        this.id = id;
        this.email = email;
    }
}
