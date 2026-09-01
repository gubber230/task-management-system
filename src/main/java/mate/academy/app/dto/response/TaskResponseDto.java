package mate.academy.app.dto.response;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;

public record TaskResponseDto(
        Long id,
        String name,
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDate dueDate,
        Long projectId,
        Long assigneeId,
        Set<LabelResponseDto> labelResponseDtoSet
) {
}
