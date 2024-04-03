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
    @Column(name = "rental_id")
    private Long rentalId;
    @Column(name = "session_url")
    private URL sessionUrl; //URL for the payment session with payment provider
    @Column(name = "session_id")
    private String sessionId; //ID of the payment session
    @Column(name = "amount_to_pay")
    private BigDecimal amountToPay; //calculated rental total price
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    public enum Status {
        PENDING, //в очікуванні
        PAID // //оплачено
    }

    public enum Type {
        PAYMENT, // оплата
        FINE // штраф
    }
}
