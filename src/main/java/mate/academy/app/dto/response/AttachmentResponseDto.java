package mate.academy.app.dto.response;

import java.time.LocalDateTime;

public record AttachmentResponseDto(
        Long id,
        Long taskId,
        String DropboxFileId,
        String fileName,
        LocalDateTime uploadDate
) {
}
