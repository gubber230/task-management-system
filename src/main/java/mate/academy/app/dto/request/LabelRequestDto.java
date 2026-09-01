package mate.academy.app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LabelRequestDto(
        @NotBlank
        String name,
        @NotBlank
        String color
) {
}
