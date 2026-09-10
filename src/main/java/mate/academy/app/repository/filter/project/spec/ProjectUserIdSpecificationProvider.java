package mate.academy.app.repository.filter.project.spec;

import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.filter.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectUserIdSpecificationProvider
        implements SpecificationProvider<Project, ProjectSearchParameters> {
    @Override
    public String getKey() {
        return "userId";
    }

    @Override
    public Specification<Project> getSpecification(ProjectSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("user").get("id"), param.getUserId());
    }
}
