package mate.academy.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.response.AttachmentResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.exception.FileOperationException;
import mate.academy.app.mapper.AttachmentMapper;
import mate.academy.app.model.Attachment;
import mate.academy.app.repository.AttachmentRepository;
import mate.academy.app.service.AttachmentService;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskService taskService;
    private final FileStorageService storageService;

    @Override
    public AttachmentResponseDto uploadAttachment(Long taskId, MultipartFile file, Long userId) {
        taskService.checkTaskAccessPermission(taskId, userId);

        String path = "/tasks/" + taskId + "/" + file.getOriginalFilename();
        String fileId;
        try (InputStream in = file.getInputStream()) {
            fileId = storageService.upload(path, in);
        } catch (IOException e) {
            throw new FileOperationException("Cannot read the provided file: "
                    + file.getOriginalFilename(), e);
        }

        Attachment attachment = attachmentMapper.toEntity(
                taskId, fileId, file.getOriginalFilename());

        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public InputStream downloadAttachment(Long taskId, Long userId) {
        taskService.checkTaskAccessPermission(taskId, userId);

        Attachment attachment = attachmentRepository.findByTaskId(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        return storageService.download(attachment.getDropboxFileId());
    }

    @Override
    public void deleteAttachment(Long taskId, Long userId) {
        taskService.checkTaskAccessPermission(taskId, userId);

        Attachment attachment = attachmentRepository.findByTaskId(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        storageService.delete(attachment.getDropboxFileId());
        attachmentRepository.delete(attachment);
    }
}
