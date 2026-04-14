package mate.academy.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.external.ProjectRequestDto;
import mate.academy.app.dto.external.ProjectResponseDto;
import mate.academy.app.dto.external.ProjectUpdateRequestDto;
import mate.academy.app.model.User;
import mate.academy.app.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for managing projects")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "Get current user accessible projects")
    public Page<ProjectResponseDto> getProjects(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        return projectService.findAll(user.getId(), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user project by id")
    public ProjectResponseDto getProjects(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId) {
        return projectService.findById(projectId, user.getId());
    }

    @PostMapping
    @Operation(summary = "Create a project")
    public ProjectResponseDto createProject(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ProjectRequestDto requestDto
    ) {
        return projectService.create(requestDto, user.getId());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update projects name or description")
    public void updateProject(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId,
            @RequestBody @Valid ProjectUpdateRequestDto updateRequestDto
    ) {
        projectService.update(projectId, updateRequestDto, user.getId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user project")
    public void deleteProject(
            @AuthenticationPrincipal User user,
            @PathVariable Long projectId
    ) {
        projectService.delete(projectId, user.getId());
    }
}
