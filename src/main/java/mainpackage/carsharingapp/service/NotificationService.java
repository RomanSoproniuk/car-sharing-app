package mainpackage.carsharingapp.service;

import mainpackage.carsharingapp.dto.RentalResponseDto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface NotificationService {
    void sendMessageNewRentalCreation(RentalResponseDto rentalResponseDto)
            throws TelegramApiException;
}
