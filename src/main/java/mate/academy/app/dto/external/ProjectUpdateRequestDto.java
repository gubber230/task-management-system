package mate.academy.app.dto.external;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.model.enums.ProjectStatus;

public record ProjectUpdateRequestDto(
        String name,
        String description,
        LocalDate endDate,
        ProjectStatus status,
        Set<Long> userIds
) {
}
