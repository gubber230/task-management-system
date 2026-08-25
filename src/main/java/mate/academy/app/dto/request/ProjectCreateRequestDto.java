package mate.academy.app.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Set;

public record ProjectCreateRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @FutureOrPresent
        LocalDate endDate,
        @NotEmpty
        Set<Long> userIds
) {
}
