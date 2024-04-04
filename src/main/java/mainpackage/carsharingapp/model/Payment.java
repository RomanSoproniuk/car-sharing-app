package mainpackage.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "payments")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rental_id", nullable = false)
    private Long rentalId;
    @Column(name = "session_url", nullable = false)
    private URL sessionUrl;
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    @Column(name = "amount_to_pay", nullable = false)
    private BigDecimal amountToPay;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public enum Status {
        PENDING,
        PAID
    }

    public enum Type {
        PAYMENT,
        FINE
    }
}
