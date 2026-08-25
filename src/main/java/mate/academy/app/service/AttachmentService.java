package mate.academy.app.service;

import java.io.InputStream;
import mate.academy.app.dto.response.AttachmentResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentResponseDto uploadAttachment(Long taskId, MultipartFile file, Long userId);

    InputStream downloadAttachment(Long taskId, Long userId);

    void deleteAttachment(Long taskId, Long userId);
}
