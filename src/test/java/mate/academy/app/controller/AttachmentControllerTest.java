package mate.academy.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
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
import mate.academy.app.model.User;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/attachment/insert-attachment-controller-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/attachment/cleanup-attachment-controller-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class AttachmentControllerTest {

    private final Long taskId = 601L;
    private final Long taskWithoutAttachmentId = 602L;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;
    private User owner;
    private User outsider;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(601L);
        outsider = new User();
        outsider.setId(602L);
    }

    @Test
    void uploadAttachments_ValidRequest_ReturnsDto() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());

        when(storageService.upload(eq("/tasks/601/test.txt"), any(InputStream.class)))
                .thenReturn("id:new-attachment");

        mockMvc.perform(multipart("/attachments")
                        .file(file)
                        .param("taskId", taskId.toString())
                        .with(user(owner))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.txt"));
    }

    @Test
    void uploadAttachments_UserLacksTaskAccess_ReturnsForbidden() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test data".getBytes());

        mockMvc.perform(multipart("/attachments")
                        .file(file)
                        .param("taskId", taskId.toString())
                        .with(user(outsider))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(storageService);
    }

    @Test
    void downloadAttachments_ValidRequest_ReturnsFileContent() throws Exception {
        InputStream stream = new ByteArrayInputStream("file content".getBytes());
        when(storageService.download("dbx-601")).thenReturn(stream);

        mockMvc.perform(get("/attachments")
                        .param("taskId", taskId.toString())
                        .with(user(owner)))
                .andExpect(status().isOk());
    }

    @Test
    void downloadAttachments_AttachmentNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/attachments")
                        .param("taskId", taskWithoutAttachmentId.toString())
                        .with(user(owner)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAttachments_ValidRequest_ReturnsOk() throws Exception {
        mockMvc.perform(delete("/attachments")
                        .param("taskId", taskId.toString())
                        .with(user(owner))
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
