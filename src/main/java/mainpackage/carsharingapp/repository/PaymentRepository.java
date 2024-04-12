package mainpackage.carsharingapp.repository;

import java.util.List;
import mainpackage.carsharingapp.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>,
        JpaSpecificationExecutor<Payment> {
    Payment findPaymentBySessionId(String sessionId);

    List<Payment> findAllByRentalId(Long rentalId);
}
