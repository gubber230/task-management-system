package mate.academy.app.dto.request;

public record CommentCreateRequestDto(
        Long taskId,
        Long userId,
        String text
) {
}
