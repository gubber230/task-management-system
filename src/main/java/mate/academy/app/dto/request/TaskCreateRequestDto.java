package mate.academy.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import mate.academy.app.model.enums.TaskPriority;

public record TaskCreateRequestDto(
        @NotBlank
        String name,
        String description,
        @NotNull
        TaskPriority priority,
        LocalDate dueDate,
        @NotNull
        Long projectId,
        @NotNull
        Long assigneeId
) {
}
