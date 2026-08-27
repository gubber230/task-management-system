package mate.academy.app.dto.response;

import java.time.LocalDateTime;

public record AttachmentResponseDto(
        Long id,
        Long taskId,
        String dropboxFileId,
        String fileName,
        LocalDateTime uploadDate
) {
}
