package mate.academy.app.dto.external;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserRegistrationRequestDto(
        @NotBlank
        String username,
        @NotNull @Length(min = 8, max = 20)
        String password,
        @NotNull @Length(min = 8, max = 20)
        String repeatPassword,
        @NotBlank @Email
        String email,
        String firstName,
        String lastName
) {
}
