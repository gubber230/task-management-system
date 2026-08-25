package mate.academy.app.dto.response;

public record UserRegistrationResponseDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
