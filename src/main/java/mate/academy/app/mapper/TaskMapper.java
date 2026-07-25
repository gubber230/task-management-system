package mate.academy.app.mapper;

import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.external.TaskCreateRequestDto;
import mate.academy.app.dto.external.TaskUpdateRequestDto;
import mate.academy.app.dto.internal.TaskDto;
import mate.academy.app.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NOT_STARTED")
    Task toModel(TaskCreateRequestDto requestDto);

    TaskDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    void update(@MappingTarget Task task, TaskUpdateRequestDto updateRequestDto);
}
