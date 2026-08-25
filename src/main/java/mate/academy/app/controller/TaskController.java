package mate.academy.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.dto.response.TaskResponseDto;
import mate.academy.app.model.User;
import mate.academy.app.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Task", description = "Endpoints for managing tasks")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a task")
    TaskResponseDto createTask(@AuthenticationPrincipal User user,
                               @RequestBody @Valid TaskCreateRequestDto taskRequestDto) {
        return taskService.create(taskRequestDto, user.getId());
    }

    @GetMapping
    @Operation(summary = "Get current user accessible tasks")
    Page<TaskResponseDto> getTasks(@AuthenticationPrincipal User user, Pageable pageable) {
        return taskService.findAll(user.getId(), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by id")
    TaskResponseDto getTask(@PathVariable @Min(0) Long taskId, @AuthenticationPrincipal User user) {
        return taskService.findById(taskId, user.getId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    void updateTask(@PathVariable @Min(0) Long taskId,
                    @RequestBody @Valid TaskUpdateRequestDto updateRequestDto,
                    @AuthenticationPrincipal User user) {
        taskService.update(taskId, updateRequestDto, user.getId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    void deleteTask(@PathVariable @Min(0) Long id, @AuthenticationPrincipal User user) {
        taskService.delete(id, user.getId());
    }
}
