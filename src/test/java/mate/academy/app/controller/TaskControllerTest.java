package mate.academy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.dto.response.TaskResponseDto;
import mate.academy.app.model.User;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;
import mate.academy.app.security.JwtUtil;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import mate.academy.app.service.TaskService;
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
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;

    private User user;
    private final Long taskId = 1L;
    private final Long projectId = 2L;
    private final Long userId = 3L;
    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        responseDto = new TaskResponseDto(
                taskId, "Test Task", "Test Description",
                TaskPriority.MEDIUM, TaskStatus.IN_PROGRESS,
                LocalDate.now(), projectId, userId, Collections.emptySet());
    }

    @Test
    void createTask_ValidRequest_ReturnsCreatedTask() throws Exception {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "Test Task", "Test Description", TaskPriority.MEDIUM,
                LocalDate.now(), projectId, userId, Set.of());

        when(taskService.create(requestDto, userId)).thenReturn(responseDto);

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getTasks_ValidPageable_ReturnsPageOfTasks() throws Exception {
        Page<TaskResponseDto> page = new PageImpl<>(List.of(responseDto));

        when(taskService.findAll(eq(userId), any())).thenReturn(page);

        mockMvc.perform(get("/tasks").with(user(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getTask_ValidId_ReturnsTask() throws Exception {
        when(taskService.findById(taskId, userId)).thenReturn(responseDto);

        // See class-level Javadoc: currently fails due to @PathVariable name mismatch.
        mockMvc.perform(get("/tasks/{id}", taskId).with(user(user)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask_ValidRequest_ReturnsOk() throws Exception {
        TaskUpdateRequestDto updateDto = new TaskUpdateRequestDto(
                "Updated", "Updated Desc", TaskPriority.HIGH, TaskStatus.COMPLETED,
                LocalDate.now(), userId, Set.of());

        doNothing().when(taskService).update(eq(taskId), any(), eq(userId));

        // See class-level Javadoc: currently fails due to @PathVariable name mismatch.
        mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTask_ValidId_ReturnsOk() throws Exception {
        doNothing().when(taskService).deleteById(taskId, userId);

        mockMvc.perform(delete("/tasks/{id}", taskId)
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void search_ValidParameters_ReturnsListOfTasks() throws Exception {
        when(taskService.search(any(), eq(userId))).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/tasks/search")
                        .param("statuses", "IN_PROGRESS")
                        .with(user(user)))
                .andExpect(status().isOk());
    }
}
