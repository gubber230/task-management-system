package mate.academy.app.dto.external;

import java.time.LocalDate;
import java.util.Set;
import mate.academy.app.dto.internal.UserDto;
import mate.academy.app.model.enums.ProjectStatus;

public record ProjectResponseDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        ProjectStatus status,
        UserDto owner,
        Set<UserDto> users
) {
}
