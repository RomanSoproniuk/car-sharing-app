package mainpackage.carsharingapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.model.Car;
import mainpackage.carsharingapp.model.TelegramUser;
import mainpackage.carsharingapp.repository.TelegramUserRepository;
import mainpackage.carsharingapp.service.impl.TelegramBotService;
import mainpackage.carsharingapp.service.impl.TelegramNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
public class TelegramNotificationServiceTest {
    @InjectMocks
    private TelegramNotificationService telegramNotificationService;
    @Mock
    private TelegramUserRepository telegramUserRepository;
    @Mock
    private TelegramBotService telegramBotService;
    private RentalResponseDto rentalResponseDto;
    private CarResponseDto carResponseDto;
    private TelegramUser telegramUser;

    @BeforeEach
    public void setUp() {
        telegramUser = new TelegramUser(443214530L, "Bob");
        carResponseDto = new CarResponseDto(1L, "Q8", "Audi",
                Car.Type.SEDAN, 5, BigDecimal.valueOf(20L));
        rentalResponseDto = new RentalResponseDto(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), null, 1L,
                2L,carResponseDto);

    }

    @Test
    @DisplayName("""
            Send message about new rental creation
            """)
    public void sendMessageNewRentalCreation_CheckSendingMessage_Success()
            throws TelegramApiException {
        //given
        Mockito.when(telegramUserRepository.findAll()).thenReturn(List.of(telegramUser));
        //when
        telegramNotificationService.sendMessageNewRentalCreation(rentalResponseDto);
    }
}
