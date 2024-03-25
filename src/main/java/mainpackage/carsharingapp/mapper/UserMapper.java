package mainpackage.carsharingapp.mapper;

import mainpackage.carsharingapp.config.MapperConfig;
import mainpackage.carsharingapp.dto.UserProfileResponseDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User user);

    UserProfileResponseDto toUserProfileResponse(User user);
}
