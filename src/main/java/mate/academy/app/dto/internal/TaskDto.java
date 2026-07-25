package mate.academy.app.dto.internal;

import java.time.LocalDate;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;

public record TaskDto(
        Long id,
        String name,
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDate dueDate,
        Long projectId,
        Long assigneeId
) {
}
