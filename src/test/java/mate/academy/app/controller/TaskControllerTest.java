package mate.academy.app.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.model.User;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;
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
@Sql(scripts = "/sql/task/insert-task-controller-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/task/cleanup-task-controller-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class TaskControllerTest {

    private final Long taskId = 301L;
    private final Long projectId = 301L;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;
    private User owner;
    private User assignee;
    private User outsider;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(301L);
        assignee = new User();
        assignee.setId(302L);
        outsider = new User();
        outsider.setId(303L);
    }

    @Test
    void createTask_ValidRequestByOwner_ReturnsCreatedTask() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "New Task", "New Description", TaskPriority.MEDIUM,
                LocalDate.now().plusDays(3), projectId, 302L, Set.of());

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Task"))
                .andExpect(jsonPath("$.status").value("NOT_STARTED"))
                .andExpect(jsonPath("$.projectId").value(301))
                .andExpect(jsonPath("$.assigneeId").value(302));
    }

    @Test
    void createTask_NonOwnerRequests_ReturnsForbidden() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "New Task", "New Description", TaskPriority.MEDIUM,
                LocalDate.now().plusDays(3), projectId, 302L, Set.of());

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(assignee))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasks_ValidPageable_ReturnsPageOfTasks() throws Exception {
        mockMvc.perform(get("/tasks").with(user(assignee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Integration Task"));
    }

    @Test
    void getTask_OwnerRequests_ReturnsTask() throws Exception {
        mockMvc.perform(get("/tasks/{id}", taskId).with(user(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Task"));
    }

    @Test
    void getTask_OutsiderRequests_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/tasks/{id}", taskId).with(user(outsider)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTask_NotExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", 999_999L).with(user(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_ValidRequestByMember_UpdatesTask() throws Exception {
        TaskUpdateRequestDto updateDto = new TaskUpdateRequestDto(
                "Updated Task", "Updated Desc", TaskPriority.LOW, TaskStatus.COMPLETED,
                LocalDate.now().plusDays(1), 302L, Set.of());

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(user(assignee))
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/tasks/{id}", taskId).with(user(owner)))
                .andExpect(jsonPath("$.name").value("Updated Task"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void updateTask_OutsiderRequests_ReturnsForbidden() throws Exception {
        TaskUpdateRequestDto updateDto = new TaskUpdateRequestDto(
                "Updated Task", "Updated Desc", TaskPriority.LOW, TaskStatus.COMPLETED,
                LocalDate.now().plusDays(1), 302L, Set.of());

        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(user(outsider))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTask_ValidIdByOwner_RemovesTask() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", taskId)
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/tasks/{id}", taskId).with(user(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_NonOwnerRequests_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", taskId)
                        .with(user(assignee))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void search_ValidParameters_ReturnsListOfTasks() throws Exception {
        mockMvc.perform(get("/tasks/search")
                        .param("statuses", "IN_PROGRESS")
                        .with(user(assignee)))
                .andExpect(status().isOk());
    }
}
