package mate.academy.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.response.AttachmentResponseDto;
import mate.academy.app.model.User;
import mate.academy.app.service.AttachmentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Attachment", description = "Endpoints for managing attachments")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public AttachmentResponseDto uploadAttachments(@RequestParam Long taskId,
                                                  @RequestParam MultipartFile file,
                                                  @AuthenticationPrincipal User user) {
        return attachmentService.uploadAttachment(taskId, file, user.getId());
    }

    @GetMapping
    public InputStream downloadAttachments(
            @RequestParam Long taskId,
            @AuthenticationPrincipal User user) {
        return attachmentService.downloadAttachment(taskId, user.getId());
    }

    @DeleteMapping
    public void deleteAttachments(
            @RequestParam Long taskId,
            @AuthenticationPrincipal User user) {
        attachmentService.deleteAttachment(taskId, user.getId());
    }
}
