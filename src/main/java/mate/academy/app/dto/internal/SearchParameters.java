package mate.academy.app.dto.internal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SearchParameters {
    private String[] names;
    private Long userId;
}
