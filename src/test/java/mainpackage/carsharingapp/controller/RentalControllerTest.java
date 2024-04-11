package mainpackage.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.RentalRequestDto;
import mainpackage.carsharingapp.dto.RentalResponseDto;
import mainpackage.carsharingapp.dto.RentalSearchParameters;
import mainpackage.carsharingapp.dto.ReturnRentalRequestDto;
import mainpackage.carsharingapp.model.Car;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private RentalRequestDto rentalRequestDto;
    private RentalResponseDto rentalResponseDto;
    private RentalResponseDto rentalResponseDtoWithActualReturnDate;
    private CarResponseDto carResponseDto;
    private ReturnRentalRequestDto returnRentalRequestDto;
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
        returnRentalRequestDto = new ReturnRentalRequestDto(LocalDate.of(2024,4,25));
        rentalRequestDto = new RentalRequestDto(LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 4, 30), 1L, 1L);
        rentalResponseDto = new RentalResponseDto(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 4, 30), null, 1L, 1L, carResponseDto);
        rentalResponseDtoWithActualReturnDate = new RentalResponseDto(1L, LocalDate.of(2024, 4, 10),
                LocalDate.of(2024, 4, 30), LocalDate.of(2024, 4, 25), 1L, 1L, carResponseDto);
        carResponseDto = new CarResponseDto(1L, "X5", "BMW",
                Car.Type.SEDAN, 5, BigDecimal.valueOf(10L));
    }

    @Test
    @DisplayName("""
            Create correct new rental
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/rentals/add-car-and-rentals-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals-from-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addRental_CorrectAddNewRental_Success() throws Exception {
        //given
        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);
        RentalResponseDto expected = rentalResponseDto;
        //when
        MvcResult result = mockMvc.perform(post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        RentalResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), RentalResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "carResponseDto");
    }

    @Test
    @DisplayName("""
            Add actual return date
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/rentals/add-car-and-rentals-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals-from-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addActualReturnDate_AddCorrectReturnDate_Success() throws Exception {
        //given
        long rentalId = 1L;
        String jsonRequest = objectMapper.writeValueAsString(returnRentalRequestDto);
        RentalResponseDto expected = rentalResponseDtoWithActualReturnDate;
        //when
        MvcResult result = mockMvc.perform(post("/rentals/{rentalId}/return", rentalId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        RentalResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), RentalResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "carResponseDto");
    }

    @Test
    @DisplayName("""
            Get rental by id for personal user
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/rentals/add-car-and-rentals-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals-from-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getRentalByIdForPersonalUser_ReturnRentalById_Success() throws Exception {
        //given
        long rentalId = 1L;
        RentalResponseDto expected = rentalResponseDto;
        //when
        MvcResult result = mockMvc.perform(get("/rentals/{rentalId}", rentalId))
                .andExpect(status().isOk())
                .andReturn();
        //then
        RentalResponseDto actual = objectMapper.readValue(result
                .getResponse().getContentAsString(), RentalResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "carResponseDto");
    }

    @Test
    @DisplayName("""
            Get rental by id for personal user
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/rentals/add-car-and-rentals-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals-from-rentals-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void searchRentalsByParams_ReturnRentalsByParams_Success() throws Exception {
        //given
        RentalResponseDto expected = rentalResponseDto;
        String jsonRequest = objectMapper.writeValueAsString(rentalSearchParameters);
        //when
        MvcResult result = mockMvc.perform(get("/rentals/search"))
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<RentalResponseDto> actualList =
                Arrays.stream(objectMapper.readValue(result.getResponse()
                        .getContentAsString(), RentalResponseDto[].class))
                .toList();
        Assertions.assertNotNull(actualList.get(0));
        EqualsBuilder.reflectionEquals(expected, actualList.get(0));
    }
}
