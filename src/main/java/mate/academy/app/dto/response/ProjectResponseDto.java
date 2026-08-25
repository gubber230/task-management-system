package mate.academy.app.dto.response;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.model.enums.ProjectStatus;

public record ProjectResponseDto(
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
