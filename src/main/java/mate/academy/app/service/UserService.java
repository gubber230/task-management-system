package mate.academy.app.service;

import mate.academy.app.dto.request.UserRegistrationRequestDto;
import mate.academy.app.dto.response.UserRegistrationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

    Page<UserRegistrationResponseDto> findAll(Pageable pageable);
}
