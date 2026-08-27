package mate.academy.app.repository.project.spec;

import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class EndDateSpecificationProvider
        implements SpecificationProvider<Project, ProjectSearchParameters> {
    @Override
    public String getKey() {
        return "endDate";
    }

    @Override
    public Specification<Project> getSpecification(ProjectSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), param.getEndDate());
    }
}
