package mainpackage.carsharingapp.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mainpackage.carsharingapp.config.BotConfig;
import mainpackage.carsharingapp.model.Rental;
import mainpackage.carsharingapp.model.TelegramUser;
import mainpackage.carsharingapp.repository.RentalRepository;
import mainpackage.carsharingapp.repository.TelegramUserRepository;
import mainpackage.carsharingapp.service.impl.TelegramBotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
public class TelegramBotServiceTest {
    @InjectMocks
    private TelegramBotService telegramBotService;
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private TelegramUserRepository telegramUserRepository;
    private BotConfig botConfig = new BotConfig();
    private Rental rentalWithoutOverdue;
    private Rental rentalWithOverdue;
    private TelegramUser telegramUser;

    @BeforeEach
    public void setUp() {
        rentalWithoutOverdue = new Rental(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), LocalDate.of(2024, 6, 20),
                1L, 2L, false, true);
        rentalWithOverdue = new Rental(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 20),
                1L, 2L, false, true);
        telegramUser = new TelegramUser(443214530L, "Bob");
        botConfig.setBotName("CarSharingPetProectBot");
        botConfig.setBotToken("7037364669:AAFA6c51IlR1CjL5lxnLGDqIFs0EWrj17LA");
    }

    @Test
    @DisplayName("""
            Test correct sending without overdue rentals
            """)
    public void sendOverdueRentals_CorrectSendWithoutOverdueRentals_Success()
            throws TelegramApiException {
        //given
        List<Rental> rentalList = new ArrayList<>();
        rentalList.add(rentalWithoutOverdue);
        ReflectionTestUtils.setField(telegramBotService, "botConfig", botConfig);
        Mockito.when(telegramUserRepository.findAll()).thenReturn(List.of(telegramUser));
        Mockito.when(rentalRepository.findAllByReturnDateAndNotCarNorReturned(Mockito.any()))
                .thenReturn(rentalList);
        //when
        telegramBotService.sendOverdueRentals();
    }

    @Test
    @DisplayName("""
            Test correct sending without overdue rentals
            """)
    public void sendOverdueRentals_CorrectSendOverdueRentals_Success()
            throws TelegramApiException {
        //given
        List<Rental> rentalList = new ArrayList<>();
        rentalList.add(rentalWithOverdue);
        ReflectionTestUtils.setField(telegramBotService, "botConfig", botConfig);
        Mockito.when(rentalRepository.findAllByReturnDateAndNotCarNorReturned(Mockito.any()))
                .thenReturn(rentalList);
        Mockito.when(telegramUserRepository.findAll()).thenReturn(List.of(telegramUser));
        //when
        telegramBotService.sendOverdueRentals();
    }
}
