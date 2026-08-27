package mate.academy.app.dto.internal;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;

@Getter
@Setter
@NoArgsConstructor
public class TaskSearchParameters extends SearchParameters {
    private TaskStatus[] statuses;
    private TaskPriority[] priorities;
    private LocalDate dueDate;
}
