package mate.academy.app.repository;

import mate.academy.app.dto.internal.SearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T, P extends SearchParameters> {
    String getKey();

    Specification<T> getSpecification(P param);
}
