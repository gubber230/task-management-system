package mate.academy.app.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.request.ProjectCreateRequestDto;
import mate.academy.app.dto.request.ProjectUpdateRequestDto;
import mate.academy.app.dto.response.ProjectResponseDto;
import mate.academy.app.model.Project;
import mate.academy.app.model.User;
import mate.academy.app.repository.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class, uses = {UserMapper.class})
public interface ProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "INITIATED")
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "toUser")
    @Mapping(source = "requestDto.userIds", target = "users", qualifiedByName = "toUsers")
    Project toModel(
            ProjectCreateRequestDto requestDto, Long ownerId,
            @Context UserRepository userRepository);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "userIds", source = "users", qualifiedByName = "toUserIds")
    ProjectResponseDto toDto(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "users", source = "userIds", qualifiedByName = "toUsers")
    void update(@MappingTarget Project oldProject,
                ProjectUpdateRequestDto updateRequestDto,
                @Context UserRepository userRepository);

    @Named("toUser")
    default User idToUser(Long userId, @Context UserRepository userRepository) {
        return userRepository.getReferenceById(userId);
    }

    @Named("toUsers")
    default Set<User> idsToUsers(Set<Long> userIds, @Context UserRepository userRepository) {
        if (userIds == null) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(userIds));
    }

    @Named("toUserIds")
    default Set<Long> usersToIds(Set<User> users) {
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
    }
}
