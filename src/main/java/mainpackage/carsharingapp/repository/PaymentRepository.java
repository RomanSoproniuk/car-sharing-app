package mainpackage.carsharingapp.repository;

import java.util.List;
import java.util.Set;
import mainpackage.carsharingapp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {
    Payment findPaymentBySessionId(String sessionId);

    @Query("SELECT p FROM payments p WHERE p.rentalId IN :rentalIds AND p.isDeleted = false")
    List<Payment> findByRentalIds(@Param("rentalIds") Set<Long> rentalIds);
}
