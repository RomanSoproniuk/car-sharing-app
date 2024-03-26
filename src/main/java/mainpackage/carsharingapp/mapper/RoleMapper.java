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
    String ROLE_CUSTOMER = "CUSTOMER";
    String ROLE_MANAGER = "MANAGER";

    @Mapping(target = "name", source = "roleName",
            qualifiedByName = "transformRoleStringToRoleName")
    Role toEntity(RoleRequestDto roleRequestDto);

    @Named("transformRoleStringToRoleName")
    default Role.RoleName transformRoleStringToRoleName(String role) {
        if (Objects.equals(ROLE_CUSTOMER.toLowerCase(), role.toLowerCase())) {
            return Role.RoleName.CUSTOMER;
        }
        if (Objects.equals(ROLE_MANAGER.toLowerCase(), role.toLowerCase())) {
            return Role.RoleName.MANAGER;
        }
        throw new RoleException("You have entered a role that does not exist, "
                + "please check your entry and try again.");
    }
}
