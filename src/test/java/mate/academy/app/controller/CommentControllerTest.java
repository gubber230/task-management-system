package mate.academy.app.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import mate.academy.app.dto.request.CommentCreateRequestDto;
import mate.academy.app.model.User;
import mate.academy.app.service.FileStorageService;
import mate.academy.app.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql(scripts = "/sql/comment/insert-comment-controller-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/comment/cleanup-comment-controller-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class CommentControllerTest {

    private final Long taskId = 501L;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FileStorageService storageService;
    @MockitoBean
    private NotificationService notificationService;
    private User member;
    private User outsider;

    @BeforeEach
    void setUp() {
        member = new User();
        member.setId(502L);
        outsider = new User();
        outsider.setId(503L);
    }

    @Test
    void createComment_ValidRequest_ReturnsCommentResponseDto() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto(taskId, 502L, "New comment");

        mockMvc.perform(post("/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(member))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New comment"))
                .andExpect(jsonPath("$.taskId").value(501))
                .andExpect(jsonPath("$.userId").value(502));
    }

    @Test
    void createComment_UserLacksTaskAccess_ReturnsForbidden() throws Exception {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto(taskId, 503L, "New comment");

        mockMvc.perform(post("/comments")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(member))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getComments_ValidTaskId_ReturnsPageOfComments() throws Exception {
        mockMvc.perform(get("/comments")
                        .param("taskId", taskId.toString())
                        .with(user(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].text").value("Existing comment"));
    }

    @Test
    void getComments_UserLacksTaskAccess_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/comments")
                        .param("taskId", taskId.toString())
                        .with(user(outsider)))
                .andExpect(status().isForbidden());
    }
}
