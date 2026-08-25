package mate.academy.app.service;

import mate.academy.app.dto.request.CommentCreateRequestDto;
import mate.academy.app.dto.response.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponseDto create(CommentCreateRequestDto requestDto);

    Page<CommentResponseDto> getAllByTaskId(Long taskId, Pageable pageable);
}
