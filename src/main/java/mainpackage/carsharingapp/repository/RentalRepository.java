package mainpackage.carsharingapp.repository;

import mainpackage.carsharingapp.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
