package mainpackage.carsharingapp.service.impl;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mainpackage.carsharingapp.config.BotConfig;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.TelegramUser;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.TelegramUserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {
    private static final long ONE_DAY = 1L;
    private static final String NO_RENTALS_OVERDUE = "No rentals overdue today!";
    private final BotConfig botConfig;
    private final RentalRepository rentalRepository;
    private final TelegramUserRepository telegramUserRepository;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (!telegramUserRepository.existsById(update.getMessage().getChatId())) {
                TelegramUser user = new TelegramUser();
                user.setId(update.getMessage().getChatId());
                user.setUserName(update.getMessage().getChat().getUserName());
                telegramUserRepository.save(user);
            }
        }
    }

    @Scheduled(cron = "${cron.time}")
    public void sendOverdueRentals() throws TelegramApiException {
        LocalDate today = LocalDate.now();
        List<Rental> allByReturnDateAndNotCarNorReturned
                = rentalRepository.findAllByReturnDateAndNotCarNorReturned(today.plusDays(ONE_DAY));
        List<TelegramUser> allTelegramUsers = telegramUserRepository.findAll();
        if (allByReturnDateAndNotCarNorReturned.isEmpty() && !allTelegramUsers.isEmpty()) {
            for (TelegramUser user : allTelegramUsers) {
                prepareAndSendMessage(user.getId(), NO_RENTALS_OVERDUE);
            }
        }
        if (!allByReturnDateAndNotCarNorReturned.isEmpty() && !allTelegramUsers.isEmpty()) {
            for (TelegramUser user : allTelegramUsers) {
                for (Rental rental : allByReturnDateAndNotCarNorReturned) {
                    prepareAndSendMessage(user.getId(), rental.toString());
                }
            }
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        execute(message);
    }

}
