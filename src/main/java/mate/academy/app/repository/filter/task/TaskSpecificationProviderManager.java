package mate.academy.app.repository.filter.task;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.internal.TaskSearchParameters;
import mate.academy.app.model.Task;
import mate.academy.app.repository.filter.SpecificationProvider;
import mate.academy.app.repository.filter.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskSpecificationProviderManager
        implements SpecificationProviderManager<Task, TaskSearchParameters> {
    private final List<SpecificationProvider<Task, TaskSearchParameters>> specProviders;

    @Override
    public SpecificationProvider<Task, TaskSearchParameters> getSpecificationProvider(
            String key) {
        return specProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find correct specification provider for key" + key));
    }
}
