package mate.academy.app.service;

import java.util.List;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.dto.request.ProjectCreateRequestDto;
import mate.academy.app.dto.request.ProjectUpdateRequestDto;
import mate.academy.app.dto.response.ProjectResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponseDto create(ProjectCreateRequestDto requestDto, Long ownerId);

    ProjectResponseDto findById(Long projectId, Long userId);

    Page<ProjectResponseDto> findAll(Long userId, Pageable pageable);

    void update(Long projectId, ProjectUpdateRequestDto updateRequestDto, Long ownerId);

    void delete(Long projectId, Long ownerId);

    List<ProjectResponseDto> search(ProjectSearchParameters searchParameters, Long userId);

    void checkProjectOwnerPermission(Long projectId, Long ownerId);

    void checkProjectAccessPermission(Long projectId, Long userId);
}
