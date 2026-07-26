package mate.academy.app.service;

import mate.academy.app.dto.external.CommentCreateRequestDto;
import mate.academy.app.dto.internal.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto create(CommentCreateRequestDto requestDto);

    Page<CommentDto> getAllByTaskId(Long taskId, Pageable pageable);
}
