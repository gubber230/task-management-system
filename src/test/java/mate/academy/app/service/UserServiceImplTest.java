package mate.academy.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.app.dto.request.UserRegistrationRequestDto;
import mate.academy.app.dto.response.UserRegistrationResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.exception.RegistrationException;
import mate.academy.app.mapper.UserMapper;
import mate.academy.app.model.Role;
import mate.academy.app.model.User;
import mate.academy.app.repository.RoleRepository;
import mate.academy.app.repository.UserRepository;
import mate.academy.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDto requestDto;
    private User user;
    private Role role;
    private UserRegistrationResponseDto responseDto;
    private final String rawPassword = "password123";
    private final String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        requestDto = new UserRegistrationRequestDto(
                "john_doe", rawPassword, rawPassword,
                "john@example.com", "John", "Doe"
        );

        role = new Role();
        role.setId(1L);
        role.setRole(Role.RoleName.USER);

        user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        responseDto = new UserRegistrationResponseDto(
                1L, "john_doe", "john@example.com", "John", "Doe"
        );
    }

    @Test
    void register_ValidRequestDto_ReturnsResponseDto() {
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userRepository.existsByUsername(requestDto.username())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserRegistrationResponseDto actual = userService.register(requestDto);

        assertEquals(responseDto, actual);
        assertEquals(encodedPassword, user.getPassword());
        assertEquals(Set.of(role), user.getRoles());
        verify(userRepository).save(user);
    }

    @Test
    void register_EmailAlreadyExists_ThrowsRegistrationException() {
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.register(requestDto));

        verifyNoInteractions(userMapper, passwordEncoder, roleRepository);
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsRegistrationException() {
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userRepository.existsByUsername(requestDto.username())).thenReturn(true);

        assertThrows(RegistrationException.class, () -> userService.register(requestDto));

        verifyNoInteractions(userMapper, passwordEncoder, roleRepository);
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_UserRoleNotFoundInDatabase_ThrowsEntityNotFoundException() {
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(userRepository.existsByUsername(requestDto.username())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(roleRepository.findByRole(Role.RoleName.USER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.register(requestDto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void findAll_ValidPageable_ReturnsPageOfResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        Page<UserRegistrationResponseDto> actual = userService.findAll(pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(responseDto, actual.getContent().getFirst());
    }

    @Test
    void findAll_NoUsers_ReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of());

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserRegistrationResponseDto> actual = userService.findAll(pageable);

        assertEquals(0, actual.getTotalElements());
        verifyNoInteractions(userMapper);
    }
}
