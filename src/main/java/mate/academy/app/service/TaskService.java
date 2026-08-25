package mate.academy.app.service;

import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.dto.response.TaskResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskResponseDto create(TaskCreateRequestDto requestDto, Long ownerId);

    Page<TaskResponseDto> findAll(Long userId, Pageable pageable);

    TaskResponseDto findById(Long taskId, Long userId);

    void update(Long taskId, TaskUpdateRequestDto updateDto, Long userId);

    void delete(Long taskId, Long userId);

    void checkTaskAccessPermission(Long taskId, Long userId);
}
