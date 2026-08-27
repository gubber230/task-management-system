package mate.academy.app.repository.project.spec;

import java.util.Arrays;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class NameSpecificationProvider
        implements SpecificationProvider<Project, ProjectSearchParameters> {
    @Override
    public String getKey() {
        return "name";
    }

    @Override
    public Specification<Project> getSpecification(ProjectSearchParameters param) {
        return (root, query, criteriaBuilder)
                -> root.get("name").in(Arrays.asList(param.getNames()));

    }
}
