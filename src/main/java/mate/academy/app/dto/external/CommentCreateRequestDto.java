package mate.academy.app.dto.external;

public record CommentCreateRequestDto(
        Long taskId,
        Long userId,
        String text
) {
}
