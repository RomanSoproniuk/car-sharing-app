package mainpackage.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import mainpackage.carsharingapp.dto.PaymentCancelResponseDto;
import mainpackage.carsharingapp.dto.PaymentRequestDto;
import mainpackage.carsharingapp.dto.PaymentResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.model.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    private static final String DEFAULT_CANCEL_MESSAGE
            = "You can pay later (but the session is available for only 24 hours)";
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private PaymentRequestDto paymentRequestDto;
    private PaymentResponseDto paymentResponseDto;
    private RentalSearchParameters rentalSearchParameters;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setUp() throws Exception {
        rentalSearchParameters = new RentalSearchParameters(new Boolean[]{true}, new Long[]{1L});
        paymentRequestDto = new PaymentRequestDto(1L, Payment.Type.PAYMENT);
        paymentResponseDto = new PaymentResponseDto(1L, 1L, new URL("http://localhost:8080/testUrl"),
                "testSessionId", BigDecimal.valueOf(1000L), Payment.Status.PENDING,
                Payment.Type.PAYMENT);
    }

    @Test
    @DisplayName("""
            Create correct payment session
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/payments/add-payment-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/payments/delete-all-payments-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void createSession_CreateCorrectSession_Success() throws Exception {
        //given
        String jsonRequest = objectMapper.writeValueAsString(paymentRequestDto);
        PaymentResponseDto expected = paymentResponseDto;
        expected.setId(2L);
        //when
        MvcResult result = mockMvc.perform(post("/payments")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        PaymentResponseDto actual
                = objectMapper.readValue(result.getResponse().getContentAsString(),
                PaymentResponseDto.class);
        Assertions.assertEquals(expected.getType(), actual.getType());
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getRentalId(), actual.getRentalId());
    }

    @Test
    @DisplayName("""
            Get payments by params
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/payments/add-payment-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/payments/delete-all-payments-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getPayments_ReturnCorrectPayments_Success() throws Exception {
        //given
        String jsonRequest = objectMapper.writeValueAsString(rentalSearchParameters);
        List<PaymentResponseDto> expectedList = List.of(paymentResponseDto);
        //when
        MvcResult result = mockMvc.perform(get("/payments/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<PaymentResponseDto> actualList = Arrays.stream(objectMapper.readValue(result
                .getResponse().getContentAsString(), PaymentResponseDto[].class)).toList();
        Assertions.assertFalse(actualList.isEmpty());
        Assertions.assertEquals(expectedList.get(0).getType(), actualList.get(0).getType());
        Assertions.assertEquals(expectedList.get(0).getId(), actualList.get(0).getId());
        Assertions.assertEquals(expectedList.get(0).getRentalId(), actualList.get(0).getRentalId());
    }

    @Test
    @DisplayName("""
            Return correct message
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/payments/add-payment-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/payments/delete-all-payments-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void cancelPayment_ReturnCorrectMessage_Success() throws Exception {
        //when
        MvcResult result = mockMvc.perform(get("/payments/cancel"))
                .andExpect(status().isOk())
                .andReturn();
        //then
        PaymentCancelResponseDto actualDto = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        PaymentCancelResponseDto.class);
        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(DEFAULT_CANCEL_MESSAGE, actualDto.getMessage());
    }
}
