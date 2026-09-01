package mate.academy.app.mapper;

import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import mate.academy.app.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    @Mapping(target = "id", ignore = true)
    Label toModel(LabelRequestDto requestDto);

    LabelResponseDto toDto(Label label);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Label label, LabelUpdateRequestDto updateRequestDto);
}
