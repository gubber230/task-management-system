package mate.academy.app.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mate.academy.app.dto.request.UserLoginRequestDto;
import mate.academy.app.dto.request.UserRegistrationRequestDto;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/user/insert-auth-controller-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/user/cleanup-auth-controller-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class AuthentificationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        UserLoginRequestDto loginRequestDto = new UserLoginRequestDto("login_user_test", "password123");

        mockMvc.perform(get("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_BlankUsername_ReturnsBadRequest() throws Exception {
        UserLoginRequestDto invalidRequest = new UserLoginRequestDto("", "password123");

        mockMvc.perform(get("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ValidRequest_ReturnsCreated() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "new_reg_user", "password123", "password123",
                "new_reg_user@example.com", "New", "User");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new_reg_user"));
    }

    @Test
    void register_PasswordsDoNotMatch_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto mismatched = new UserRegistrationRequestDto(
                "mismatch_user", "password123", "different123",
                "mismatch_user@example.com", "John", "Doe");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mismatched))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto invalidEmail = new UserRegistrationRequestDto(
                "invalid_email_user", "password123", "password123",
                "not-an-email", "John", "Doe");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidEmail))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_UsernameOrEmailTaken_ReturnsConflict() throws Exception {
        UserRegistrationRequestDto duplicate = new UserRegistrationRequestDto(
                "john_doe", "password123", "password123",
                "john@example.com", "John", "Doe");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicate))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails("admin")
    void getAll_AsAdmin_ReturnsPageOfUsers() throws Exception {
        mockMvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].username").value(hasItem("admin")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAll_AsNonAdmin_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/auth"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAll_Unauthenticated_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/auth"))
                .andExpect(status().isForbidden());
    }
}
