package mainpackage.carsharingapp.repository;

import java.time.LocalDate;
import java.util.List;
import mainpackage.carsharingapp.model.Rental;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("""
            Return correct value by query
            """)
    @Sql(scripts = {"classpath:database/rentals/add-rental-car-not-returned.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals-from-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAllByReturnDateAndNotCarNorReturned_ReturnCorrectRentals_Success() {
        List<Rental> allByReturnDateAndNotCarNorReturned
                = rentalRepository.findAllByReturnDateAndNotCarNorReturned(LocalDate
                .of(2024, 4, 29));
        Assertions.assertEquals(1, allByReturnDateAndNotCarNorReturned.size());
    }
}
