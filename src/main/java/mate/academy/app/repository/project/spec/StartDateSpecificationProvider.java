package mate.academy.app.repository.project.spec;

import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StartDateSpecificationProvider
        implements SpecificationProvider<Project, ProjectSearchParameters> {
    @Override
    public String getKey() {
        return "startDate";
    }

    public Specification<Project> getSpecification(ProjectSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"),
                param.getStartDate());
    }
}
