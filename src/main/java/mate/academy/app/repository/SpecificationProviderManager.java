package mate.academy.app.repository;

import mate.academy.app.dto.internal.SearchParameters;

public interface SpecificationProviderManager<T, P extends SearchParameters> {
    SpecificationProvider<T, P> getSpecificationProvider(String key);
}
