package mate.academy.app.mapper;

import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.response.AttachmentResponseDto;
import mate.academy.app.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uploadDate", expression = "java(LocalDateTime.now())")
    Attachment toEntity(Long taskId, String dropboxFileId, String fileName);

    AttachmentResponseDto toDto(Attachment attachment);
}
