package mate.academy.app.repository.filter.task;

import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.internal.TaskSearchParameters;
import mate.academy.app.model.Task;
import mate.academy.app.repository.filter.SpecificationBuilder;
import mate.academy.app.repository.filter.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskSpecificationBuilder
        implements SpecificationBuilder<Task, TaskSearchParameters> {
    private final SpecificationProviderManager<Task, TaskSearchParameters> spManager;

    @Override
    public Specification<Task> build(TaskSearchParameters searchParameters) {
        Specification<Task> spec =
                Specification.where((root, query, cb) -> cb.conjunction());

        if (searchParameters.getUserId() != null) {
            spec = spec.and(spManager.getSpecificationProvider("userId")
                    .getSpecification(searchParameters));
        }

        if (searchParameters.getNames() != null && searchParameters.getNames().length > 0) {
            spec = spec.and(spManager.getSpecificationProvider("name")
                    .getSpecification(searchParameters));
        }

        if (searchParameters.getStatuses() != null && searchParameters.getStatuses().length > 0) {
            spec = spec.and(spManager.getSpecificationProvider("status")
                    .getSpecification(searchParameters));
        }

        if (searchParameters.getPriorities() != null
                && searchParameters.getPriorities().length > 0) {
            spec = spec.and(spManager.getSpecificationProvider("priority")
                    .getSpecification(searchParameters));
        }

        if (searchParameters.getDueDate() != null) {
            spec = spec.and(spManager.getSpecificationProvider("dueDate")
                    .getSpecification(searchParameters));
        }

        if (searchParameters.getLabelIds() != null && searchParameters.getLabelIds().length > 0) {
            spec = spec.and(spManager.getSpecificationProvider("labelIds")
                    .getSpecification(searchParameters));
        }

        return spec;
    }
}
