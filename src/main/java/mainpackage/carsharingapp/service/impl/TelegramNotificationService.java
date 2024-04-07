package mainpackage.carsharingapp.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.model.TelegramUser;
import mainpackage.carsharingapp.repository.TelegramUserRepository;
import mainpackage.carsharingapp.service.NotificationService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramUserRepository telegramUserRepository;
    private final TelegramBotService telegramBotService;

    @Override
    public void sendMessageNewRentalCreation(RentalResponseDto rentalResponseDto)
            throws TelegramApiException {
        List<TelegramUser> allTelegramUsers = telegramUserRepository.findAll();
        if (allTelegramUsers.isEmpty()) {
            return;
        }
        for (TelegramUser user : allTelegramUsers) {
            Long chatId = user.getId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(rentalResponseDto.toString());
            sendMessage.setChatId(String.valueOf(chatId));
            telegramBotService.execute(sendMessage);
        }
    }
}
