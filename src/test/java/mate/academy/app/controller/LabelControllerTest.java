package mate.academy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import mate.academy.app.security.JwtUtil;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.LabelService;
import mate.academy.app.service.NotificationService;
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
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LabelService labelService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;

    private final Long labelId = 1L;
    private LabelResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new LabelResponseDto(labelId, "Bug", "#FF0000");
    }

    @Test
    @WithUserDetails("admin")
    void create_AsAdmin_ReturnsCreatedLabel() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto("Bug", "#FF0000");

        when(labelService.create(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/labels")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_AsNonAdmin_ReturnsForbidden() throws Exception {
        LabelRequestDto requestDto = new LabelRequestDto("Bug", "#FF0000");

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
    @WithMockUser(roles = "USER")
    void getAll_ValidPageable_ReturnsPageOfLabels() throws Exception {
        Page<LabelResponseDto> page = new PageImpl<>(List.of(responseDto));

        when(labelService.getALl(any())).thenReturn(page);

        mockMvc.perform(get("/labels"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void update_AsAdminValidId_ReturnsOk() throws Exception {
        LabelUpdateRequestDto updateDto = new LabelUpdateRequestDto("Feature", "#00FF00");

        doNothing().when(labelService).update(eq(labelId), any());

        mockMvc.perform(put("/labels/{id}", labelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_AsNonAdmin_ReturnsForbidden() throws Exception {
        LabelUpdateRequestDto updateDto = new LabelUpdateRequestDto("Feature", "#00FF00");

        mockMvc.perform(put("/labels/{id}", labelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("admin")
    void delete_AsAdminValidId_ReturnsOk() throws Exception {
        doNothing().when(labelService).delete(labelId);

        mockMvc.perform(delete("/labels/{id}", labelId).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_AsNonAdmin_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/labels/{id}", labelId).with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
