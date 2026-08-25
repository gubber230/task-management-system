package mate.academy.app.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.model.enums.ProjectStatus;

public record ProjectUpdateRequestDto(
        @NotBlank
        @Size(max = 50)
        String name,
        @Size(max = 1000)
        String description,
        @FutureOrPresent
        LocalDate endDate,
        @NotNull
        ProjectStatus status,
        @NotEmpty
        Set<Long> userIds
) {
}
