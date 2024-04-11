package mainpackage.carsharingapp.repository;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import mainpackage.carsharingapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User expected;

    @BeforeEach
    public void setUp() {
        String userEmail = "bob@gmail.com";
        expected = new User(2L, userEmail, "Bob", "Alison", "12345678", false);
    }

    @Test
    @DisplayName("""
            Return correct user by email
            """)
    @Sql(scripts = {"classpath:database/users/add-bob-user-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/users/delete-bob-user-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findUserByEmail_ReturnCorrectUser_Success() {
        String userEmail = "bob@gmail.com";
        Optional<User> actual = userRepository.findByEmail(userEmail);
        if (actual.isEmpty()) {
            throw new EntityNotFoundException("User not found by email " + userEmail);
        }
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Return correct user by id
            """)
    @Sql(scripts = {"classpath:database/users/add-bob-user-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/users/delete-bob-user-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findUserById_ReturnCorrectUser_Success() {
        Long userId = 1L;
        User actual = userRepository.findById(userId).orElseThrow(()
                -> new EntityNotFoundException("User not found by id " + userId));
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
