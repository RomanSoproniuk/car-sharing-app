package mainpackage.carsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch,
        UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto value,
                           ConstraintValidatorContext context) {
        return value.getPassword() != null
                && Objects.equals(value.getPassword(), value.getRepeatPassword());
    }
}
