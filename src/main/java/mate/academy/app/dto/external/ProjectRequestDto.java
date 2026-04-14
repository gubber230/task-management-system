package mate.academy.app.dto.external;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

public record ProjectRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        LocalDate endDate,
        Set<Long> userIds
) {
}
