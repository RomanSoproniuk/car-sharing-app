package mainpackage.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mainpackage.carsharingapp.dto.UserLoginRequestDto;
import mainpackage.carsharingapp.dto.UserLoginResponseDto;
import mainpackage.carsharingapp.dto.UserRegistrationRequestDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private UserRegistrationRequestDto userRegistrationRequestDto;
    private UserResponseDto userResponseDto;
    private UserLoginRequestDto userLoginRequestDto;

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
        userLoginRequestDto = new UserLoginRequestDto("admin@gmail.com", "12345678");
        userRegistrationRequestDto = new UserRegistrationRequestDto("bob@gmail.com", "Bob",
                "Alison", "123456789", "123456789");
        userResponseDto = new UserResponseDto(2L, "bob@gmail.com");
    }

    @Test
    @DisplayName("""
            Return correct JWT token
            """)
    public void login_ReturnCorrectJwtToken_Success() throws Exception {
        //given
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);
        //when
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        UserLoginResponseDto userLoginResponseDto = objectMapper.readValue(result
                .getResponse().getContentAsString(), UserLoginResponseDto.class);
        Assertions.assertNotNull(userLoginResponseDto);
    }

    @Test
    @DisplayName("""
            Register new user
            """)
    @Sql(scripts = {"classpath:database/users/delete-user-from-db-afret-registration.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void registerUser_CorrectRegisterUser_Success() throws Exception {
        //given
        UserResponseDto expected = userResponseDto;
        String jsonRequest = objectMapper.writeValueAsString(userRegistrationRequestDto);
        //when
        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        UserResponseDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), UserResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
