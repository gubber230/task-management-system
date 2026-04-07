package mate.academy.app.mapper;

import mate.academy.app.config.MapperConfig;
import mate.academy.app.dto.external.UserRegistrationRequestDto;
import mate.academy.app.dto.external.UserRegistrationResponseDto;
import mate.academy.app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserRegistrationResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRegistrationRequestDto requestDto);
}
