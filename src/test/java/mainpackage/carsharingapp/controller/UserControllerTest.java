package mainpackage.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mainpackage.carsharingapp.dto.RoleRequestDto;
import mainpackage.carsharingapp.dto.UserProfileResponseDto;
import mainpackage.carsharingapp.dto.UserResponseDto;
import mainpackage.carsharingapp.dto.UserUpdateProfileRequestDto;
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
public class UserControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private RoleRequestDto roleRequestDto;
    private UserResponseDto userResponseDto;
    private UserProfileResponseDto userProfileResponseDto;
    private UserProfileResponseDto updatedUserProfileResponseDto;
    private UserUpdateProfileRequestDto userUpdateProfileRequestDto;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        userUpdateProfileRequestDto = new UserUpdateProfileRequestDto("john@gmail.com",
                "John", "Doe");
        updatedUserProfileResponseDto
                = new UserProfileResponseDto("john@gmail.com", "John", "Dou", null);
        userProfileResponseDto
                = new UserProfileResponseDto("alice@gmail.com", "Alice",
                "Bobson", null);
        roleRequestDto = new RoleRequestDto("MANAGER");
        userResponseDto = new UserResponseDto(2L, "alice@gmail.com");
    }

    @Test
    @DisplayName("""
            Correct update user role for user
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/users/add-alice-user-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/users/delete-alice-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateUserRole_CorrectUpdateUserRole_Success() throws Exception {
        //given
        UserResponseDto expected = userResponseDto;
        Long userId = 1L;
        String jsonRequest = objectMapper.writeValueAsString(roleRequestDto);
        //when
        MvcResult result = mockMvc.perform(put("/users/{id}/role", userId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        UserResponseDto actual
                = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Update profile info
            """)
    @WithMockUser(username = "alice@gmail.com", roles = {"CUSTOMER"})
    @Sql(scripts = {"classpath:database/users/add-alice-user-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/users/delete-john-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void updateProfileInfo_CorrectUpdateProfileInfo_Success() throws Exception {
        //given
        UserProfileResponseDto expected = updatedUserProfileResponseDto;
        String jsonRequest = objectMapper.writeValueAsString(userUpdateProfileRequestDto);
        //when
        MvcResult result = mockMvc.perform(put("/users/me")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        //then
        UserProfileResponseDto actual
                = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserProfileResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Return correct profile info
            """)
    @WithMockUser(username = "admin@gmail.com", roles = {"MANAGER"})
    @Sql(scripts = {"classpath:database/users/add-alice-user-to-db.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/users/delete-alice-from-db.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getProfileInfo_ReturnCorrectProfileInfo_Success() throws Exception {
        //given
        UserProfileResponseDto expected = userProfileResponseDto;
        //when
        MvcResult result = mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andReturn();
        //then
        UserProfileResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserProfileResponseDto.class);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
