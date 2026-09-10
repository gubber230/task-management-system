package mate.academy.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.request.UserLoginRequestDto;
import mate.academy.app.dto.request.UserRegistrationRequestDto;
import mate.academy.app.dto.response.UserLoginResponseDto;
import mate.academy.app.dto.response.UserRegistrationResponseDto;
import mate.academy.app.exception.RegistrationException;
import mate.academy.app.lib.annotations.FieldMatch;
import mate.academy.app.security.AuthenticationService;
import mate.academy.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for managing users authentication")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/auth")
public class AuthentificationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    @Operation(summary = "Login user")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @PostMapping("/registration")
    @Operation(summary = "Registered a new user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDto register(
            @RequestBody @Valid @FieldMatch(
                    first = "password",
                    second = "repeatPassword",
                    message = "Passwords do not match")
            UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all users")
    public Page<UserRegistrationResponseDto> getAll(Pageable pageable) {
        return userService.findAll(pageable);
    }
}
