package mate.academy.app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import mate.academy.app.dto.request.UserLoginRequestDto;
import mate.academy.app.dto.request.UserRegistrationRequestDto;
import mate.academy.app.dto.response.UserLoginResponseDto;
import mate.academy.app.dto.response.UserRegistrationResponseDto;
import mate.academy.app.exception.RegistrationException;
import mate.academy.app.security.AuthenticationService;
import mate.academy.app.security.JwtUtil;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import mate.academy.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthentificationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationService authenticationService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;

    private MockMvc mockMvc;
    private UserRegistrationRequestDto registrationRequestDto;
    private UserLoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        registrationRequestDto = new UserRegistrationRequestDto(
                "john_doe", "password123", "password123",
                "john@example.com", "John", "Doe");
        loginRequestDto = new UserLoginRequestDto("john_doe", "password123");
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        UserLoginResponseDto responseDto = new UserLoginResponseDto("jwt-token");

        when(authenticationService.authenticate(loginRequestDto)).thenReturn(responseDto);

        mockMvc.perform(get("/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
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
        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto(
                1L, "john_doe", "john@example.com", "John", "Doe");

        when(userService.register(registrationRequestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void register_PasswordsDoNotMatch_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto mismatched = new UserRegistrationRequestDto(
                "john_doe", "password123", "different123",
                "john@example.com", "John", "Doe");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(mismatched))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto invalidEmail = new UserRegistrationRequestDto(
                "john_doe", "password123", "password123",
                "not-an-email", "John", "Doe");

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidEmail))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_UsernameOrEmailTaken_ReturnsConflict() throws Exception {
        when(userService.register(registrationRequestDto))
                .thenThrow(new RegistrationException("Username or email has been taken"));

        mockMvc.perform(post("/auth/registration")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registrationRequestDto))
                        .with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails("admin")
    void getAll_AsAdmin_ReturnsPageOfUsers() throws Exception {
        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto(
                1L, "john_doe", "john@example.com", "John", "Doe");
        Page<UserRegistrationResponseDto> page = new PageImpl<>(List.of(responseDto));

        when(userService.findAll(org.mockito.ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(get("/auth"))
                .andExpect(status().isOk());
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