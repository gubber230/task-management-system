package mate.academy.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.external.UserRegistrationRequestDto;
import mate.academy.app.dto.external.UserRegistrationResponseDto;
import mate.academy.app.exception.RegistrationException;
import mate.academy.app.mapper.UserMapper;
import mate.academy.app.model.Role;
import mate.academy.app.model.User;
import mate.academy.app.repository.RoleRepository;
import mate.academy.app.repository.UserRepository;
import mate.academy.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    public static final Role.RoleName ROLE_NAME = Role.RoleName.USER;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())
                || userRepository.existsByUsername(requestDto.username())) {
            throw new RegistrationException(
                    "Username or email has been taken"
            );
        }
        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        Role userRole = roleRepository.findByRole(ROLE_NAME)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can not find role in database: " + ROLE_NAME));
        user.setRoles(Set.of(userRole));
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public Page<UserRegistrationResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }
}
