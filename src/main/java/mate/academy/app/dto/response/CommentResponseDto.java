package mate.academy.app.dto.response;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long taskId,
        Long userId,
        String text,
        LocalDateTime timeStamp
) {
}
