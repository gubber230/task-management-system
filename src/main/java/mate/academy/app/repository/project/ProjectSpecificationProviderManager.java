package mate.academy.app.repository.project;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.model.Project;
import mate.academy.app.repository.SpecificationProvider;
import mate.academy.app.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectSpecificationProviderManager
        implements SpecificationProviderManager<Project, ProjectSearchParameters> {
    private final List<SpecificationProvider<Project, ProjectSearchParameters>> specProviders;

    @Override
    public SpecificationProvider<Project, ProjectSearchParameters> getSpecificationProvider(
            String key) {
        return specProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find correct specification provider for key" + key));
    }
}
