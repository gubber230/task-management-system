package mate.academy.app.dto.internal;

public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
