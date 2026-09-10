package mate.academy.app.repository.filter.task.spec;

import java.util.Arrays;
import mate.academy.app.dto.internal.TaskSearchParameters;
import mate.academy.app.model.Task;
import mate.academy.app.repository.filter.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskNameSpecificationProvider
        implements SpecificationProvider<Task, TaskSearchParameters> {
    @Override
    public String getKey() {
        return "name";
    }

    @Override
    public Specification<Task> getSpecification(TaskSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> root.get("name").in(Arrays.asList(param.getNames()));

    }
}
