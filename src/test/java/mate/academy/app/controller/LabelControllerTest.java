package mate.academy.app.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
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
@Sql(scripts = "/sql/label/insert-label-controller-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/label/cleanup-label-controller-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class LabelControllerTest {

    private final Long labelId = 401L;
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
    @WithUserDetails("admin")
    void create_AsAdmin_ReturnsCreatedLabel() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto("Enhancement", "#123456");

        mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Enhancement"))
                .andExpect(jsonPath("$.color").value("#123456"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_AsNonAdmin_ReturnsForbidden() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto("Enhancement", "#123456");

        mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_Unauthenticated_ReturnsUnauthorized() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto("Enhancement", "#123456");

        mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("admin")
    void create_BlankName_ReturnsBadRequest() throws Exception {
        LabelRequestDto invalidRequest = new LabelRequestDto("", "#FF0000");

        mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAll_ValidPageable_ReturnsPageOfLabels() throws Exception {
        mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].name").value(hasItem("Bug")));
    }

    @Test
    @WithUserDetails("admin")
    void update_AsAdminValidId_ReturnsOk() throws Exception {
        LabelUpdateRequestDto updateDto = new LabelUpdateRequestDto("Updated Label", "#654321");

        mockMvc.perform(put("/labels/{id}", labelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/labels"))
                .andExpect(jsonPath("$.content[*].name").value(hasItem("Updated Label")));
    }

    @Test
    @WithUserDetails("admin")
    void update_NotExistingId_ReturnsNotFound() throws Exception {
        LabelUpdateRequestDto updateDto = new LabelUpdateRequestDto("Updated Label", "#654321");

        mockMvc.perform(put("/labels/{id}", 999_999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_AsNonAdmin_ReturnsForbidden() throws Exception {
        LabelUpdateRequestDto updateDto = new LabelUpdateRequestDto("Updated Label", "#654321");

        mockMvc.perform(put("/labels/{id}", labelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    void delete_AsAdminValidId_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/labels/{id}", labelId).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_AsNonAdmin_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/labels/{id}", labelId).with(csrf()))
                .andExpect(status().isForbidden());
    }
}
