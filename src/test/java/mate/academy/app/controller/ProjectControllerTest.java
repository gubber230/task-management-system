package mate.academy.app.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.dto.request.ProjectCreateRequestDto;
import mate.academy.app.dto.request.ProjectUpdateRequestDto;
import mate.academy.app.model.User;
import mate.academy.app.model.enums.ProjectStatus;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/project/insert-project-controller-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/project/cleanup-project-controller-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class ProjectControllerTest {

    private final Long projectId = 201L;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;
    private User owner;
    private User member;
    private User outsider;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(201L);
        member = new User();
        member.setId(202L);
        outsider = new User();
        outsider.setId(203L);
    }

    @Test
    void createProject_ValidRequest_ReturnsCreatedProject() throws Exception {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "Integration Project - New", "Created via integration test",
                LocalDate.now().plusDays(5), Set.of(202L));

        mockMvc.perform(post("/projects")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Project - New"))
                .andExpect(jsonPath("$.ownerId").value(201))
                .andExpect(jsonPath("$.status").value("INITIATED"))
                .andExpect(jsonPath("$.userIds[0]").value(202));
    }

    @Test
    void getProjects_ValidPageable_ReturnsPageOfProjects() throws Exception {
        mockMvc.perform(get("/projects").with(user(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Integration Project"));
    }

    @Test
    void getProjectById_OwnerRequests_ReturnsProject() throws Exception {
        mockMvc.perform(get("/projects/{id}", projectId).with(user(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(201));
    }

    @Test
    void getProjectById_MemberRequests_ReturnsProject() throws Exception {
        mockMvc.perform(get("/projects/{id}", projectId).with(user(member)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectById_OutsiderRequests_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/projects/{id}", projectId).with(user(outsider)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProjectById_NotExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/projects/{id}", 999_999L).with(user(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProject_ValidRequestByOwner_PersistsChanges() throws Exception {
        ProjectUpdateRequestDto updateDto = new ProjectUpdateRequestDto(
                "Updated Name", "Updated Desc", LocalDate.now().plusDays(20),
                ProjectStatus.COMPLETED, Set.of(202L));

        mockMvc.perform(patch("/projects/{id}", projectId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/projects/{id}", projectId).with(user(owner)))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateProject_NonOwnerRequests_ReturnsForbidden() throws Exception {
        ProjectUpdateRequestDto updateDto = new ProjectUpdateRequestDto(
                "Updated Name", "Updated Desc", LocalDate.now().plusDays(20),
                ProjectStatus.COMPLETED, Set.of(202L));

        mockMvc.perform(patch("/projects/{id}", projectId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(user(member))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProject_ValidIdByOwner_RemovesProject() throws Exception {
        mockMvc.perform(delete("/projects/{id}", projectId)
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/projects/{id}", projectId).with(user(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_NonOwnerRequests_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/projects/{id}", projectId)
                        .with(user(outsider))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void search_ValidParameters_ReturnsListOfProjects() throws Exception {
        mockMvc.perform(get("/projects/search")
                        .param("names", "Integration Project")
                        .with(user(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Integration Project"));
    }
}
