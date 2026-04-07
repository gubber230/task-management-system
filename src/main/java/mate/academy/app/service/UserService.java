package mate.academy.app.service;

import mate.academy.app.dto.external.UserRegistrationRequestDto;
import mate.academy.app.dto.external.UserRegistrationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    Page<UserRegistrationResponseDto> findAll(Pageable pageable);
}
