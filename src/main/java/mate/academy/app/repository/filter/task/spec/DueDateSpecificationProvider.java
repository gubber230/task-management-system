package mate.academy.app.repository.filter.task.spec;

import mate.academy.app.dto.internal.TaskSearchParameters;
import mate.academy.app.model.Task;
import mate.academy.app.repository.filter.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DueDateSpecificationProvider
        implements SpecificationProvider<Task, TaskSearchParameters> {
    @Override
    public String getKey() {
        return "dueDate";
    }

    @Override
    public Specification<Task> getSpecification(TaskSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), param.getDueDate());
    }
}
