package mate.academy.app.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.app.dto.internal.TaskSearchParameters;
import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.dto.response.TaskResponseDto;
import mate.academy.app.event.TaskAssignedEvent;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.mapper.LabelMapper;
import mate.academy.app.mapper.TaskMapper;
import mate.academy.app.model.Task;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;
import mate.academy.app.repository.LabelRepository;
import mate.academy.app.repository.TaskRepository;
import mate.academy.app.repository.filter.task.TaskSpecificationBuilder;
import mate.academy.app.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    private final Long taskId = 1L;
    private final Long projectId = 2L;
    private final Long userId = 3L;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private TaskSpecificationBuilder specificationBuilder;
    @Mock
    private LabelMapper labelMapper;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private TaskServiceImpl taskService;
    private Task task;
    private TaskResponseDto responseDto;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(taskId);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setProjectId(projectId);
        task.setAssigneeId(userId);

        responseDto = new TaskResponseDto(
                taskId, "Test Task", "Test Description",
                TaskPriority.MEDIUM, TaskStatus.IN_PROGRESS,
                LocalDate.now(), projectId, userId, Collections.emptySet()
        );
    }

    @Test
    void create_ValidRequestDto_ReturnsTaskResponseDto() {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "Test Task", "Test Description", TaskPriority.MEDIUM,
                LocalDate.now(), projectId, userId, Set.of()
        );

        doNothing().when(projectService).checkProjectOwnerPermission(projectId, userId);
        when(taskMapper.toModel(requestDto, labelRepository)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task, labelMapper)).thenReturn(responseDto);

        TaskResponseDto actual = taskService.create(requestDto, userId);

        assertEquals(responseDto, actual);
        verify(eventPublisher).publishEvent(any(TaskAssignedEvent.class));
    }

    @Test
    void findAll_ValidUserIdAndPageable_ReturnsPageOfTaskResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findAllByAssigneeId(userId, pageable)).thenReturn(taskPage);
        when(taskMapper.toDto(task, labelMapper)).thenReturn(responseDto);

        Page<TaskResponseDto> actual = taskService.findAll(userId, pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(responseDto, actual.getContent().getFirst());
    }

    @Test
    void findById_ValidId_ReturnsTaskResponseDto() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(projectService).checkProjectAccessPermission(projectId, userId);
        when(taskMapper.toDto(task, labelMapper)).thenReturn(responseDto);

        TaskResponseDto actual = taskService.findById(taskId, userId);

        assertEquals(responseDto, actual);
    }

    @Test
    void findById_NotValidId_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.findById(taskId, userId));
    }

    @Test
    void update_ValidRequestDto_Success() {
        TaskUpdateRequestDto updateDto = new TaskUpdateRequestDto(
                "Updated", "Updated Desc", TaskPriority.HIGH, TaskStatus.COMPLETED,
                LocalDate.now(), userId, Set.of()
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(projectService).checkProjectAccessPermission(projectId, userId);

        assertDoesNotThrow(() -> taskService.update(taskId, updateDto, userId));

        verify(taskMapper).update(task, updateDto, labelRepository);
        verify(eventPublisher).publishEvent(any(TaskAssignedEvent.class));
    }

    @Test
    void update_NotValidId_ThrowsEntityNotFoundException() {
        TaskUpdateRequestDto updateDto = new TaskUpdateRequestDto(
                "Updated", "Updated Desc", TaskPriority.HIGH, TaskStatus.COMPLETED,
                LocalDate.now(), userId, Set.of()
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.update(taskId, updateDto, userId));
    }

    @Test
    void delete_ValidId_Success() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(projectService).checkProjectOwnerPermission(projectId, userId);

        assertDoesNotThrow(() -> taskService.deleteById(taskId, userId));

        verify(taskRepository).delete(task);
    }

    @Test
    void delete_NotValidId_ThrowsEntityNotFoundException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.deleteById(taskId, userId));
    }

    @Test
    void search_ValidParameters_ReturnsListOfTaskResponseDto() {
        TaskSearchParameters searchParams = new TaskSearchParameters();
        searchParams.setStatuses(new TaskStatus[]{TaskStatus.IN_PROGRESS});
        Specification<Task> spec = (root, query, cb) -> null;

        when(specificationBuilder.build(searchParams)).thenReturn(spec);
        when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of(task));
        when(taskMapper.toDto(task, labelMapper)).thenReturn(responseDto);

        List<TaskResponseDto> actual = taskService.search(searchParams, userId);

        assertEquals(1, actual.size());
        assertEquals(responseDto, actual.getFirst());
        assertEquals(userId, searchParams.getUserId());
    }

    @Test
    void checkTaskAccessPermission_ValidId_Success() {
        when(taskRepository.findProjectIdById(taskId)).thenReturn(Optional.of(projectId));
        doNothing().when(projectService).checkProjectAccessPermission(projectId, userId);

        assertDoesNotThrow(() -> taskService.checkTaskAccessPermission(taskId, userId));
    }

    @Test
    void checkTaskAccessPermission_NotValidId_ThrowsEntityNotFoundException() {
        when(taskRepository.findProjectIdById(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.checkTaskAccessPermission(taskId, userId));
    }

    @Test
    void create_UserLacksProjectOwnerPermission_ThrowsAccessDeniedException() {
        TaskCreateRequestDto requestDto = new TaskCreateRequestDto(
                "Test Task", "Test Description", TaskPriority.MEDIUM,
                LocalDate.now(), projectId, userId, Set.of()
        );

        doThrow(new AccessDeniedException("Access denied"))
                .when(projectService).checkProjectOwnerPermission(projectId, userId);

        assertThrows(AccessDeniedException.class, () -> taskService.create(requestDto, userId));

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void findById_UserLacksProjectAccess_ThrowsAccessDeniedException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doThrow(new AccessDeniedException("Access denied"))
                .when(projectService).checkProjectAccessPermission(projectId, userId);

        assertThrows(AccessDeniedException.class, () -> taskService.findById(taskId, userId));
    }

    @Test
    void update_UserLacksProjectAccess_ThrowsAccessDeniedException() {
        TaskUpdateRequestDto updateDto = new TaskUpdateRequestDto(
                "Updated", "Updated Desc", TaskPriority.HIGH, TaskStatus.COMPLETED,
                LocalDate.now(), userId, Set.of()
        );

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doThrow(new AccessDeniedException("Access denied"))
                .when(projectService).checkProjectAccessPermission(projectId, userId);

        assertThrows(AccessDeniedException.class,
                () -> taskService.update(taskId, updateDto, userId));

        verify(taskMapper, never()).update(any(), any(), any());
    }

    @Test
    void deleteById_UserLacksProjectOwnerPermission_ThrowsAccessDeniedException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doThrow(new AccessDeniedException("Access denied"))
                .when(projectService).checkProjectOwnerPermission(projectId, userId);

        assertThrows(AccessDeniedException.class, () -> taskService.deleteById(taskId, userId));

        verify(taskRepository, never()).deleteById(any());
    }
}
