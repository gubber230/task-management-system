package mate.academy.app.repository;

import mate.academy.app.dto.internal.SearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T, P extends SearchParameters> {
    Specification<T> build(P searchParameters);
}
