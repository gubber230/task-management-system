package mate.academy.app.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import mate.academy.app.dto.request.CommentCreateRequestDto;
import mate.academy.app.dto.response.CommentResponseDto;
import mate.academy.app.model.User;
import mate.academy.app.security.JwtUtil;
import mate.academy.app.service.CommentService;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentService commentService;
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
    void createComment_ValidRequest_ReturnsCommentResponseDto() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto(taskId, userId, "Test comment");
        CommentResponseDto responseDto = new CommentResponseDto(
                1L, taskId, userId, "Test comment", LocalDateTime.now());

        when(commentService.create(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(user))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getComments_ValidTaskId_ReturnsPageOfComments() throws Exception {
        CommentResponseDto responseDto = new CommentResponseDto(
                1L, taskId, userId, "Test comment", LocalDateTime.now());
        Page<CommentResponseDto> page = new PageImpl<>(List.of(responseDto));

        when(commentService.getAllByTaskId(
                        org.mockito.ArgumentMatchers.eq(taskId),
                        org.mockito.ArgumentMatchers.eq(userId),
                        org.mockito.ArgumentMatchers.any()))
                .thenReturn(page);

        mockMvc.perform(get("/comments")
                        .param("taskId", taskId.toString())
                        .with(user(user)))
                .andExpect(status().isOk());
    }
}
