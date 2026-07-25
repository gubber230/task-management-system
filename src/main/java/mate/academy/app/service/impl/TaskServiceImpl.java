package mate.academy.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.external.TaskCreateRequestDto;
import mate.academy.app.dto.external.TaskUpdateRequestDto;
import mate.academy.app.dto.internal.TaskDto;
import mate.academy.app.mapper.TaskMapper;
import mate.academy.app.model.Task;
import mate.academy.app.repository.TaskRepository;
import mate.academy.app.service.ProjectService;
import mate.academy.app.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto create(TaskCreateRequestDto requestDto, Long ownerId) {
        projectService.checkOwnerPermission(requestDto.projectId(), ownerId);
        Task savedTask = taskRepository.save(taskMapper.toModel(requestDto));
        return taskMapper.toDto(savedTask);
    }

    @Override
    public Page<TaskDto> findAll(Long userId, Pageable pageable) {
        return taskRepository.findAllByAssigneeId(userId, pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public TaskDto findById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException(
                "Task with ID " + taskId + " does not exist"
        ));
        projectService.checkAccessPermission(task.getProjectId(), userId);
        return taskMapper.toDto(task);
    }

    @Override
    public void update(Long taskId, TaskUpdateRequestDto updateDto, Long userId) {
        Task oldTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Task with ID " + taskId + " does not exist"
                ));
        taskMapper.update(oldTask, updateDto);
    }

    @Override
    public void delete(Long taskId, Long userId) {
        Task taskToDelete = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(
                "Task with ID " + taskId + " does not exist"
        ));
        projectService.checkOwnerPermission(taskToDelete.getProjectId(), userId);
        taskRepository.delete(taskToDelete);
    }
}
