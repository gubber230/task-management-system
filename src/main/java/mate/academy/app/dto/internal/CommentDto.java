package mate.academy.app.dto.internal;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        Long taskId,
        Long userId,
        String text,
        LocalDateTime timeStamp
) {
}
