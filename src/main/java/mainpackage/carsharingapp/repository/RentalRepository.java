package mainpackage.carsharingapp.repository;

import java.time.LocalDate;
import java.util.List;
import mainpackage.carsharingapp.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {
    @Query("FROM rentals r WHERE r.returnDate <= :nextDate AND r.actualReturnDate IS NULL")
    List<Rental> findAllByReturnDateAndNotCarNorReturned(LocalDate nextDate);
}
