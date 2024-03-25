package mainpackage.carsharingapp.repository;

import java.util.Optional;
import mainpackage.carsharingapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Role.RoleName roleName);
}
