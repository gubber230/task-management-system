package mate.academy.app.dto.external;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;

public record TaskUpdateRequestDto(
        @NotBlank
        @Size(max = 50)
        String name,
        @Size(max = 1000)
        String description,
        @NotNull
        TaskPriority priority,
        @NotNull
        TaskStatus status,
        @FutureOrPresent
        LocalDate dueDate,
        @NotNull
        Long assigneeId
) {
}
