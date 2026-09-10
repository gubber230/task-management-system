package mate.academy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import mate.academy.app.dto.response.AttachmentResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.model.User;
import mate.academy.app.security.JwtUtil;
import mate.academy.app.service.AttachmentService;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentService attachmentService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;

    private User user;
    private final Long taskId = 1L;
    private final Long userId = 2L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
    }

    @Test
    void uploadAttachments_ValidRequest_ReturnsDto() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());
        AttachmentResponseDto responseDto = new AttachmentResponseDto(
                1L, taskId, "id:123456", "test.txt", LocalDateTime.now());

        when(attachmentService.uploadAttachment(eq(taskId), any(), eq(userId)))
                .thenReturn(responseDto);

        mockMvc.perform(multipart("/attachments")
                        .file(file)
                        .param("taskId", taskId.toString())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"));
    }

    @Test
    void downloadAttachments_ValidRequest_ReturnsFileContent() throws Exception {
        InputStream stream = new ByteArrayInputStream("file content".getBytes());

        when(attachmentService.downloadAttachment(taskId, userId)).thenReturn(stream);

        mockMvc.perform(get("/attachments")
                        .param("taskId", taskId.toString())
                        .with(user(user)))
                .andExpect(status().isOk());
    }

    @Test
    void downloadAttachments_AttachmentNotFound_ReturnsNotFound() throws Exception {
        when(attachmentService.downloadAttachment(taskId, userId))
                .thenThrow(new EntityNotFoundException("Attachment not found"));

        mockMvc.perform(get("/attachments")
                        .param("taskId", taskId.toString())
                        .with(user(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAttachments_ValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/attachments")
                        .param("taskId", taskId.toString())
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void uploadAttachments_Unauthenticated_ReturnsUnauthorized() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());

        mockMvc.perform(multipart("/attachments")
                        .file(file)
                        .param("taskId", taskId.toString())
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }
}
