package mate.academy.app.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.internal.TaskSearchParameters;
import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.dto.response.TaskResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.mapper.LabelMapper;
import mate.academy.app.mapper.TaskMapper;
import mate.academy.app.model.Task;
import mate.academy.app.repository.LabelRepository;
import mate.academy.app.repository.TaskRepository;
import mate.academy.app.repository.filter.task.TaskSpecificationBuilder;
import mate.academy.app.service.ProjectService;
import mate.academy.app.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final TaskMapper taskMapper;
    private final TaskSpecificationBuilder specificationBuilder;
    private final LabelMapper labelMapper;
    private final LabelRepository labelRepository;

    @Override
    public TaskResponseDto create(TaskCreateRequestDto requestDto, Long ownerId) {
        projectService.checkProjectOwnerPermission(requestDto.projectId(), ownerId);
        Task savedTask = taskRepository.save(taskMapper.toModel(requestDto, labelRepository));
        return taskMapper.toDto(savedTask, labelMapper);
    }

    @Override
    public Page<TaskResponseDto> findAll(Long userId, Pageable pageable) {
        return taskRepository.findAllByAssigneeId(userId, pageable)
                .map(task -> taskMapper.toDto(task, labelMapper));
    }

    @Override
    public TaskResponseDto findById(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException(
                "Task with ID " + taskId + " does not exist"
        ));
        projectService.checkProjectAccessPermission(task.getProjectId(), userId);
        return taskMapper.toDto(task, labelMapper);
    }

    @Override
    public void update(Long taskId, TaskUpdateRequestDto updateDto, Long userId) {
        Task oldTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Task with ID " + taskId + " does not exist"
                ));
        taskMapper.update(oldTask, updateDto, labelRepository);
    }

    @Override
    public void delete(Long taskId, Long userId) {
        Task taskToDelete = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Task with ID " + taskId + " does not exist"
                ));
        projectService.checkProjectOwnerPermission(taskToDelete.getProjectId(), userId);
        taskRepository.delete(taskToDelete);
    }

    @Override
    public List<TaskResponseDto> search(TaskSearchParameters searchParameters, Long userId) {
        Specification<Task> specification = specificationBuilder.build(searchParameters);
        return taskRepository.findAll(specification)
                .stream()
                .map(task -> taskMapper.toDto(task, labelMapper))
                .toList();
    }

    @Override
    public void checkTaskAccessPermission(Long taskId, Long userId) {
        projectService.checkProjectAccessPermission(
                taskRepository.findProjectIdById(taskId).orElseThrow(
                        () -> new EntityNotFoundException(
                                "Task with id:" + taskId + " do not exist.")),
                userId);
    }
}
