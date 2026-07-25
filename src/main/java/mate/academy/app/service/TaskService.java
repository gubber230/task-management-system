package mate.academy.app.service;

import mate.academy.app.dto.external.TaskCreateRequestDto;
import mate.academy.app.dto.external.TaskUpdateRequestDto;
import mate.academy.app.dto.internal.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto create(TaskCreateRequestDto requestDto, Long ownerId);

    Page<TaskDto> findAll(Long userId, Pageable pageable);

    TaskDto findById(Long taskId, Long userId);

    void update(Long taskId, TaskUpdateRequestDto updateDto, Long userId);

    void delete(Long taskId, Long userId);
}
