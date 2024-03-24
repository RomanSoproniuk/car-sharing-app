package mainpackage.carsharingapp.repository;

import java.util.Optional;
import mainpackage.carsharingapp.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM users u LEFT JOIN FETCH u.roles WHERE u.email =: email")
    Optional<User> findByEmail(@Value("email") String email);

    @Query("SELECT u FROM users u LEFT JOIN FETCH u.roles WHERE u.id =: id")
    Optional<User> findAllById(@Value("id") Long id);
}
