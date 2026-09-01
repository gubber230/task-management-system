package mate.academy.app.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import mate.academy.app.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class AttachmentServiceImpl implements AttachmentService {
    private final DbxClientV2 dropboxClient;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskService taskService;

    @Override
    public AttachmentResponseDto uploadAttachment(Long taskId, MultipartFile file, Long userId) {
        taskService.checkTaskAccessPermission(taskId, userId);
        FileMetadata metadata;
        try (InputStream in = file.getInputStream()) {
            metadata = dropboxClient.files().uploadBuilder(
                            "/tasks/" + taskId + "/" + file.getOriginalFilename())
                    .uploadAndFinish(in);
        } catch (IOException e) {
            throw new FileOperationException("Cannot read the provided file: "
                    + file.getOriginalFilename(), e);
        } catch (DbxException e) {
            throw new FileOperationException("Failed to upload file to Dropbox API", e);
        }

        Attachment attachment = attachmentMapper.toEntity(
                taskId, metadata.getId(), file.getOriginalFilename());

        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public InputStream downloadAttachment(Long taskId, Long userId) {
        taskService.checkTaskAccessPermission(taskId, userId);

        Attachment attachment = attachmentRepository.findByTaskId(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            dropboxClient.files()
                    .downloadBuilder(attachment.getDropboxFileId())
                    .download(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (DbxException | IOException e) {
            throw new FileOperationException("Failed to download file from Dropbox", e);
        }
    }

    @Override
    public void deleteAttachment(Long taskId, Long userId) {
        taskService.checkTaskAccessPermission(taskId, userId);

        Attachment attachment = attachmentRepository.findByTaskId(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));

        try {
            dropboxClient.files().deleteV2(attachment.getDropboxFileId());
        } catch (DbxException e) {
            throw new FileOperationException("Failed to delete file from Dropbox", e);
        }

        attachmentRepository.delete(attachment);
    }
}
