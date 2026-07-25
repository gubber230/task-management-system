package mate.academy.app.service;

import mate.academy.app.dto.external.ProjectCreateRequestDto;
import mate.academy.app.dto.external.ProjectResponseDto;
import mate.academy.app.dto.external.ProjectUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponseDto create(ProjectCreateRequestDto requestDto, Long ownerId);

    ProjectResponseDto findById(Long projectId, Long userId);

    Page<ProjectResponseDto> findAll(Long userId, Pageable pageable);

    void update(Long projectId, ProjectUpdateRequestDto updateRequestDto, Long ownerId);

    void delete(Long projectId, Long ownerId);

    void checkOwnerPermission(Long projectId, Long ownerId);

    void checkAccessPermission(Long projectId, Long userId);
}
