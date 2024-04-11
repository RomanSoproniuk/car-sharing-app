package mainpackage.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import mainpackage.carsharingapp.dto.CarRequestDto;
import mainpackage.carsharingapp.dto.CarResponseDto;
import mainpackage.carsharingapp.dto.CarsInventoryRequestDto;
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
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private CarRequestDto createCarRequestDto;
    private CarResponseDto createFirstCarResponseDto;
    private CarResponseDto createSecondCarResponseDto;
    private CarsInventoryRequestDto inventoryRequestDtoForUpdate;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    public void setUp() {
        inventoryRequestDtoForUpdate = new CarsInventoryRequestDto(50);
        createCarRequestDto = new CarRequestDto("X5", "BMW",
                Car.Type.SEDAN, 5, BigDecimal.valueOf(10L));
        createFirstCarResponseDto = new CarResponseDto(1L, "X5", "BMW",
                Car.Type.SEDAN, 5, BigDecimal.valueOf(10L));
        createSecondCarResponseDto = new CarResponseDto(1L, "Laguna", "Renault",
                Car.Type.UNIVERSAL, 10, BigDecimal.valueOf(20L));
    }

    @Test
    @DisplayName("""
            Correct add car to db
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/cars/delete-all-cars-from-db.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addCar_CorrectCreateCar_Success() throws Exception {
        //given
        CarRequestDto carRequestDto = createCarRequestDto;
        CarResponseDto expected = createFirstCarResponseDto;
        String jsonRequest = objectMapper.writeValueAsString(carRequestDto);
        //when
        MvcResult result = mockMvc.perform(post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        CarResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("""
            Return correct quantity of cars and correct cars
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/cars/add-cars-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/delete-all-cars-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAllCars_ReturnAllCarsFromDb_Success() throws Exception {
        //given
        List<CarResponseDto> expectedList
                = List.of(createFirstCarResponseDto, createFirstCarResponseDto);
        //when
        MvcResult result = mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<CarResponseDto> actualList = Arrays.stream(objectMapper
                .readValue(result.getResponse().getContentAsString(),
                CarResponseDto[].class)).toList();
        Assertions.assertEquals(expectedList.size(), actualList.size());
        EqualsBuilder.reflectionEquals(expectedList.get(0), actualList.get(0), "id");
        EqualsBuilder.reflectionEquals(expectedList.get(1), actualList.get(1), "id");
    }

    @Test
    @DisplayName("""
            Return correct car by id
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/cars/add-cars-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/delete-all-cars-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getCarById_ReturnCarById_Success() throws Exception {
        //given
        long carsId = 1L;
        CarResponseDto expected = createFirstCarResponseDto;
        //when
        MvcResult result = mockMvc.perform(get("/cars/{carsId}", carsId))
                .andExpect(status().isOk())
                .andReturn();
        //then
        CarResponseDto actual
                = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("""
            Success update car inventory
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/cars/add-cars-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/delete-all-cars-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCarsInventoryById_CorrectUpdateCarInventory_Success() throws Exception {
        //given
        long carsId = 1L;
        String jsonRequest = objectMapper.writeValueAsString(inventoryRequestDtoForUpdate);
        //when
        mockMvc.perform(patch("/cars/{carsId}", carsId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Test
    @DisplayName("""
            Success update car
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/cars/add-cars-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/delete-all-cars-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateCar_CorrectUpdateCar_Success() throws Exception {
        //given
        long carsId = 2L;
        CarRequestDto carUpdateRequestDto = createCarRequestDto;
        CarResponseDto expected = createFirstCarResponseDto;
        String jsonRequest = objectMapper.writeValueAsString(carUpdateRequestDto);
        //when
        MvcResult result = mockMvc.perform(put("/cars/{carsId}", carsId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        CarResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), CarResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
        Assertions.assertEquals(carsId, actual.getId());
    }

    @Test
    @DisplayName("""
            Delete car by id
            """)
    @WithMockUser(username = "admin", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/cars/add-cars-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/cars/delete-all-cars-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void deleteCarById_CorrectDeleteCar_Success() throws Exception {
        //given
        long carsId = 1L;
        //when
        mockMvc.perform(delete("/cars/{carsId}", carsId))
                .andExpect(status().isNoContent());
    }
}
