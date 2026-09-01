package mate.academy.app.repository.filter.project.spec;

import java.util.Arrays;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.filter.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StatusSpecificationProvider
        implements SpecificationProvider<Project, ProjectSearchParameters> {
    @Override
    public String getKey() {
        return "status";
    }

    @Override
    public Specification<Project> getSpecification(ProjectSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> root.get("status").in(Arrays.asList(param.getStatuses()));
    }
}

