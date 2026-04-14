package mate.academy.app.dto.internal;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.model.enums.ProjectStatus;

public record ProjectDto(
        Long id,
        Long ownerId,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status,
        Set<Long> userIds
) {
}
