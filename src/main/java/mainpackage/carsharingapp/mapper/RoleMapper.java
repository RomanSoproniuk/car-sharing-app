package mainpackage.carsharingapp.mapper;

import java.util.Objects;
import mainpackage.carsharingapp.config.MapperConfig;
import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.exceptions.RoleException;
import mainpackage.carsharingapp.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface RoleMapper {
    String ROLE_USER = "USER";
    String ROLE_ADMIN = "ADMIN";

    @Mapping(target = "name", source = "roleName",
            qualifiedByName = "transformRoleStringToRoleName")
    Role toEntity(RoleRequestDto roleRequestDto);

    @Named("transformRoleStringToRoleName")
    default Role.RoleName transformRoleStringToRoleName(String role) {
        if (Objects.equals(ROLE_USER.toLowerCase(), role.toLowerCase())) {
            return Role.RoleName.ADMIN;
        }
        if (Objects.equals(ROLE_ADMIN.toLowerCase(), role.toLowerCase())) {
            return Role.RoleName.USER;
        }
        throw new RoleException("You have entered a role that does not exist, "
                + "please check your entry and try again.");
    }
}
