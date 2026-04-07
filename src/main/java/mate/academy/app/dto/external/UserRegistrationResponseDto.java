package mate.academy.app.dto.external;

public record UserRegistrationResponseDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
