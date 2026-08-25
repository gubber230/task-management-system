package mate.academy.app.mapper;

import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.request.CommentCreateRequestDto;
import mate.academy.app.dto.response.CommentResponseDto;
import mate.academy.app.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timeStamp", expression = "java(java.time.LocalDateTime.now())")
    Comment toModel(CommentCreateRequestDto requestDto);

    CommentResponseDto toDto(Comment comment);
}
