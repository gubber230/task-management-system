package mate.academy.app.repository.project;

import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.SpecificationBuilder;
import mate.academy.app.repository.SpecificationProviderManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectSpecificationBuilder
        implements SpecificationBuilder<Project, ProjectSearchParameters> {
    private final SpecificationProviderManager<Project, ProjectSearchParameters> spManager;

    @Override
    public Specification<Project> build(ProjectSearchParameters searchParameters) {
        Specification<Project> spec =
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

        if (searchParameters.getEndDate() != null) {
            spec = spec.and(spManager.getSpecificationProvider("endDate")
                    .getSpecification(searchParameters));
        }

        if (searchParameters.getStartDate() != null) {
            spec = spec.and(spManager.getSpecificationProvider("startDate")
                    .getSpecification(searchParameters));
        }

        return spec;
    }
}
