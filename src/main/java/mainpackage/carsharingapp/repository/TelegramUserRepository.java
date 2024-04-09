package mainpackage.carsharingapp.repository;

import mainpackage.carsharingapp.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
}
