package mainpackage.carsharingapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "telegram_users")
@Getter
@Setter
public class TelegramUser {
    @Id
    private Long id;
    private String userName;
}
