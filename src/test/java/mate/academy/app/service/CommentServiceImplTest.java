package mate.academy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import mate.academy.app.dto.request.CommentCreateRequestDto;
import mate.academy.app.dto.response.CommentResponseDto;
import mate.academy.app.mapper.CommentMapper;
import mate.academy.app.model.Comment;
import mate.academy.app.repository.CommentRepository;
import mate.academy.app.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    private final Long commentId = 1L;
    private final Long taskId = 2L;
    private final Long userId = 3L;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private TaskService taskService;
    @InjectMocks
    private CommentServiceImpl commentService;
    private Comment comment;
    private CommentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(commentId);
        comment.setTaskId(taskId);
        comment.setUserId(userId);
        comment.setText("Test comment");
        comment.setTimeStamp(LocalDateTime.now());

        responseDto = new CommentResponseDto(
                commentId, taskId, userId, "Test comment", comment.getTimeStamp()
        );
    }

    @Test
    void create_ValidRequestDto_ReturnsCommentResponseDto() {
        CommentCreateRequestDto requestDto = new CommentCreateRequestDto(taskId, userId, "Test comment");

        when(commentMapper.toModel(requestDto)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto actual = commentService.create(requestDto);

        assertEquals(responseDto, actual);

        verify(commentMapper).toModel(requestDto);
        verify(commentMapper).toDto(comment);
    }

    @Test
    void getAllByTaskId_ValidTaskIdAndPageable_ReturnsPageOfCommentResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment));

        when(commentRepository.findAllByTaskId(taskId, pageable)).thenReturn(commentPage);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        Page<CommentResponseDto> actual = commentService.getAllByTaskId(taskId, userId, pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(responseDto, actual.getContent().getFirst());

        verify(commentRepository).findAllByTaskId(taskId, pageable);
    }

    @Test
    void create_UserLacksTaskAccess_ThrowsAccessDeniedException() {
        CommentCreateRequestDto requestDto =
                new CommentCreateRequestDto(taskId, userId, "Test comment");

        doThrow(new AccessDeniedException("Access denied"))
                .when(taskService).checkTaskAccessPermission(taskId, userId);

        assertThrows(AccessDeniedException.class, () -> commentService.create(requestDto));

        verifyNoInteractions(commentMapper, commentRepository);
    }

    @Test
    void getAllByTaskId_UserLacksTaskAccess_ThrowsAccessDeniedException() {
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new AccessDeniedException("Access denied"))
                .when(taskService).checkTaskAccessPermission(taskId, userId);

        assertThrows(AccessDeniedException.class,
                () -> commentService.getAllByTaskId(taskId, userId, pageable));

        verifyNoInteractions(commentRepository);
    }
}
