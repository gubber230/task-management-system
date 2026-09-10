package mate.academy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import mate.academy.app.dto.request.ProjectCreateRequestDto;
import mate.academy.app.dto.request.ProjectUpdateRequestDto;
import mate.academy.app.dto.response.ProjectResponseDto;
import mate.academy.app.model.User;
import mate.academy.app.model.enums.ProjectStatus;
import mate.academy.app.security.JwtUtil;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import mate.academy.app.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;

    private User user;
    private final Long projectId = 1L;
    private final Long userId = 2L;
    private ProjectResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        responseDto = new ProjectResponseDto(
                projectId, userId, "Name", "Description",
                LocalDate.now(), LocalDate.now().plusDays(1),
                ProjectStatus.INITIATED, Set.of(userId));
    }

    @Test
    void createProject_ValidRequest_ReturnsCreatedProject() throws Exception {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "Name", "Description", LocalDate.now().plusDays(1), Set.of(userId));

        when(projectService.create(requestDto, userId)).thenReturn(responseDto);

        mockMvc.perform(post("/projects")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getProjects_ValidPageable_ReturnsPageOfProjects() throws Exception {
        Page<ProjectResponseDto> page = new PageImpl<>(List.of(responseDto));

        when(projectService.findAll(eq(userId), any())).thenReturn(page);

        mockMvc.perform(get("/projects").with(user(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectById_ValidId_ReturnsProject() throws Exception {
        when(projectService.findById(projectId, userId)).thenReturn(responseDto);

        // See class-level Javadoc: currently fails due to @PathVariable name mismatch.
        mockMvc.perform(get("/projects/{id}", projectId).with(user(user)))
                .andExpect(status().isOk());
    }

    @Test
    void updateProject_ValidRequest_ReturnsOk() throws Exception {
        ProjectUpdateRequestDto updateDto = new ProjectUpdateRequestDto(
                "Updated", "Updated Desc", LocalDate.now().plusDays(2),
                ProjectStatus.IN_PROGRESS, Set.of(userId));

        doNothing().when(projectService).update(eq(projectId), any(), eq(userId));

        // See class-level Javadoc: same @PathVariable mismatch as getProjectById.
        mockMvc.perform(patch("/projects/{id}", projectId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProject_ValidId_ReturnsOk() throws Exception {
        doNothing().when(projectService).delete(projectId, userId);

        // See class-level Javadoc: same @PathVariable mismatch as getProjectById.
        mockMvc.perform(delete("/projects/{id}", projectId)
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void search_ValidParameters_ReturnsListOfProjects() throws Exception {
        when(projectService.search(any(), eq(userId))).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/projects/search")
                        .param("names", "Test Project")
                        .with(user(user)))
                .andExpect(status().isOk());
    }
}
