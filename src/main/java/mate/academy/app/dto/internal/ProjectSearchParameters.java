package mate.academy.app.dto.internal;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mate.academy.app.model.enums.ProjectStatus;

@Getter
@Setter
@NoArgsConstructor
public class ProjectSearchParameters extends SearchParameters {
    private ProjectStatus[] statuses;
    private LocalDate startDate;
    private LocalDate endDate;
}
