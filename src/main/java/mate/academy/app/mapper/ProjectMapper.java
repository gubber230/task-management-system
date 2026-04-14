package mate.academy.app.mapper;

import java.util.HashSet;
import java.util.Set;
import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.external.ProjectRequestDto;
import mate.academy.app.dto.external.ProjectResponseDto;
import mate.academy.app.dto.external.ProjectUpdateRequestDto;
import mate.academy.app.model.Project;
import mate.academy.app.model.User;
import mate.academy.app.repository.UserRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class})
public interface ProjectMapper {
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "INITIATED")
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "toUser")
    @Mapping(source = "requestDto.userIds", target = "users", qualifiedByName = "toUsers")
    Project toModel(
            ProjectRequestDto requestDto, Long ownerId, @Context UserRepository userRepository);

    ProjectResponseDto toDto(Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "users", ignore = true)
    void update(@MappingTarget Project oldProject,
                ProjectUpdateRequestDto updateRequestDto,
                @Context UserRepository userRepository);

    @Named("toUser")
    default User idToUser(Long userId, @Context UserRepository userRepository) {
        return userRepository.getReferenceById(userId);
    }

    @Named("toUsers")
    default Set<User> idToUsers(Set<Long> userIds, @Context UserRepository userRepository) {
        if (userIds == null) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(userIds));
    }

    @AfterMapping
    default void appendNewUsers(@MappingTarget Project oldProject,
                                ProjectUpdateRequestDto updateRequestDto,
                                @Context UserRepository userRepository) {
        if (updateRequestDto.userIds() != null && !updateRequestDto.userIds().isEmpty()) {
            oldProject.getUsers().addAll(
                    userRepository.findAllById(updateRequestDto.userIds()));
        }
    }
}
