package mainpackage.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity(name = "cars")
@Getter
@Setter
@SQLDelete(sql = "UPDATE cars SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction(value = "is_deleted = FALSE")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "model", nullable = false)
    private String model;
    @Column(name = "brand",nullable = false)
    private String brand;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;
    @Column(name = "inventory", nullable = false)
    private int inventory;
    @Column(name = "daily_fee", nullable = false)
    private BigDecimal dailyFee = BigDecimal.ZERO;
    @Column (name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public Car() {
    }

    public Car(Long id, String model, String brand, Type type, int inventory, BigDecimal dailyFee,
               boolean isDeleted) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.inventory = inventory;
        this.dailyFee = dailyFee;
        this.isDeleted = isDeleted;
    }

    public enum Type {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
