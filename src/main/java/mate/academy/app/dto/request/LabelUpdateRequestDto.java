package mate.academy.app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LabelUpdateRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String color
) {
}
