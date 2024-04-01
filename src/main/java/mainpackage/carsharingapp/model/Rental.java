package mainpackage.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "rentals")
@Getter
@Setter
@SQLDelete(sql = "UPDATE rentals SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction(value = "is_deleted = FALSE")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rental_date")
    private LocalDate rentalDate;
    @Column(name = "return_date")
    private LocalDate returnDate;
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
    @Column(name = "car_id")
    private Long carId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
