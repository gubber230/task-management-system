package mate.academy.app.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import mate.academy.app.dto.response.AttachmentResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.exception.FileOperationException;
import mate.academy.app.mapper.AttachmentMapper;
import mate.academy.app.model.Attachment;
import mate.academy.app.repository.AttachmentRepository;
import mate.academy.app.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private AttachmentMapper attachmentMapper;
    @Mock
    private TaskService taskService;
    @Mock
    private FileStorageService storageService;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private Attachment attachment;
    private AttachmentResponseDto responseDto;
    private final Long taskId = 1L;
    private final Long userId = 2L;
    private final String fileName = "test.txt";
    private final String storageFileId = "id:123456";
    private final String expectedPath = "/tasks/1/test.txt";

    @BeforeEach
    void setUp() {
        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setTaskId(taskId);
        attachment.setFileName(fileName);
        attachment.setDropboxFileId(storageFileId);
        attachment.setUploadDate(LocalDateTime.now());

        responseDto = new AttachmentResponseDto(
                1L, taskId, storageFileId, fileName, attachment.getUploadDate()
        );
    }

    @Test
    void uploadAttachment_ValidRequest_ReturnsDto() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("test data".getBytes());

        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(storageService.upload(eq(expectedPath), any(InputStream.class)))
                .thenReturn(storageFileId);
        when(attachmentMapper.toEntity(taskId, storageFileId, fileName)).thenReturn(attachment);
        when(attachmentRepository.save(attachment)).thenReturn(attachment);
        when(attachmentMapper.toDto(attachment)).thenReturn(responseDto);

        AttachmentResponseDto actual =
                attachmentService.uploadAttachment(taskId, multipartFile, userId);

        assertEquals(responseDto, actual);
        verify(taskService).checkTaskAccessPermission(taskId, userId);
        verify(storageService).upload(eq(expectedPath), any(InputStream.class));
        verify(attachmentRepository).save(attachment);
    }

    @Test
    void uploadAttachment_IoException_ThrowsFileOperationException() throws Exception {
        IOException ioException = new IOException("stream closed");

        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(multipartFile.getInputStream()).thenThrow(ioException);

        FileOperationException actual = assertThrows(FileOperationException.class,
                () -> attachmentService.uploadAttachment(taskId, multipartFile, userId));

        assertEquals(ioException, actual.getCause());
        verifyNoInteractions(storageService);
        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void uploadAttachment_StorageException_PropagatesException() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("test data".getBytes());
        FileOperationException storageException =
                new FileOperationException("Failed to upload file to Dropbox API");

        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(storageService.upload(eq(expectedPath), any(InputStream.class)))
                .thenThrow(storageException);

        assertThrows(FileOperationException.class,
                () -> attachmentService.uploadAttachment(taskId, multipartFile, userId));

        verify(attachmentRepository, never()).save(any());
    }

    @Test
    void downloadAttachment_ValidId_ReturnsInputStream() throws Exception {
        InputStream expectedStream = new ByteArrayInputStream("test data".getBytes());

        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(Optional.of(attachment));
        when(storageService.download(storageFileId)).thenReturn(expectedStream);

        InputStream actual = attachmentService.downloadAttachment(taskId, userId);

        assertArrayEquals("test data".getBytes(), actual.readAllBytes());
        verify(storageService).download(storageFileId);
    }

    @Test
    void downloadAttachment_NotValidId_ThrowsEntityNotFoundException() {
        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.downloadAttachment(taskId, userId));

        verifyNoInteractions(storageService);
    }

    @Test
    void downloadAttachment_StorageException_PropagatesException() {
        FileOperationException storageException =
                new FileOperationException("Failed to download file from Dropbox");

        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(Optional.of(attachment));
        when(storageService.download(storageFileId)).thenThrow(storageException);

        assertThrows(FileOperationException.class,
                () -> attachmentService.downloadAttachment(taskId, userId));
    }

    @Test
    void deleteAttachment_ValidId_Success() {
        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(Optional.of(attachment));

        attachmentService.deleteAttachment(taskId, userId);

        verify(storageService).delete(storageFileId);
        verify(attachmentRepository).delete(attachment);
    }

    @Test
    void deleteAttachment_NotValidId_ThrowsEntityNotFoundException() {
        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> attachmentService.deleteAttachment(taskId, userId));

        verifyNoInteractions(storageService);
        verify(attachmentRepository, never()).delete(any());
    }

    @Test
    void deleteAttachment_StorageException_ThrowsFileOperationExceptionAndSkipsRepositoryDelete() {
        FileOperationException storageException =
                new FileOperationException("Failed to delete file from Dropbox");

        doNothing().when(taskService).checkTaskAccessPermission(taskId, userId);
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(Optional.of(attachment));
        doThrow(storageException).when(storageService).delete(storageFileId);

        assertThrows(FileOperationException.class,
                () -> attachmentService.deleteAttachment(taskId, userId));

        verify(attachmentRepository, never()).delete(any());
    }
}
