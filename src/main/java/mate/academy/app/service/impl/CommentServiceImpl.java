package mate.academy.app.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.external.CommentCreateRequestDto;
import mate.academy.app.dto.internal.CommentDto;
import mate.academy.app.mapper.CommentMapper;
import mate.academy.app.model.Comment;
import mate.academy.app.repository.CommentRepository;
import mate.academy.app.service.CommentService;
import mate.academy.app.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskService taskService;

    @Override
    public CommentDto create(CommentCreateRequestDto requestDto) {
        Comment model = commentMapper.toModel(requestDto);
        return commentMapper.toDto(model);
    }

    @Override
    public Page<CommentDto> getAllByTaskId(Long taskId, Pageable pageable) {
        return commentRepository.findAllByTaskId(taskId, pageable)
                .map(commentMapper::toDto);
    }
}
