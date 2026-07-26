package mate.academy.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.external.CommentCreateRequestDto;
import mate.academy.app.dto.internal.CommentDto;
import mate.academy.app.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comment", description = "Endpoints for managing comments")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    CommentDto createComment(@RequestBody CommentCreateRequestDto requestDto) {
        return commentService.create(requestDto);
    }

    @GetMapping
    Page<CommentDto> getComments(
            @RequestParam("taskId") Long taskId,
            Pageable pageable
    ) {
        return commentService.getAllByTaskId(taskId, pageable);
    }
}
