package mate.academy.app.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.request.TaskCreateRequestDto;
import mate.academy.app.dto.request.TaskUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import mate.academy.app.dto.response.TaskResponseDto;
import mate.academy.app.model.Label;
import mate.academy.app.model.Task;
import mate.academy.app.repository.LabelRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {LabelMapper.class})
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NOT_STARTED")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "toLabels")
    Task toModel(TaskCreateRequestDto requestDto, @Context LabelRepository labelRepository);

    @Mapping(target = "labelResponseDtoSet", source = "labels", qualifiedByName = "toLabelDtoSet")
    TaskResponseDto toDto(Task task, @Context LabelMapper labelMapper);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "toLabels")
    void update(@MappingTarget Task task,
                TaskUpdateRequestDto updateRequestDto,
                @Context LabelRepository labelRepository);

    @Named("toLabels")
    default Set<Label> toLabels(Set<Long> ids, @Context LabelRepository labelRepository) {
        if (ids == null) {
            return new HashSet<>();
        }
        return new HashSet<>(labelRepository.findAllById(ids));
    }

    @Named("toLabelDtoSet")
    default Set<LabelResponseDto> toLabelDtoSet(Set<Label> labels,
                                                @Context LabelMapper labelMapper) {
        return labels.stream()
                .map(labelMapper::toDto)
                .collect(Collectors.toSet());
    }
}
