package mate.academy.app.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.dto.request.ProjectCreateRequestDto;
import mate.academy.app.dto.request.ProjectUpdateRequestDto;
import mate.academy.app.dto.response.ProjectResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.mapper.ProjectMapper;
import mate.academy.app.model.Project;
import mate.academy.app.repository.ProjectRepository;
import mate.academy.app.repository.UserRepository;
import mate.academy.app.repository.filter.project.ProjectSpecificationBuilder;
import mate.academy.app.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectSpecificationBuilder projectSpecificationBuilder;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ProjectResponseDto create(ProjectCreateRequestDto requestDto, Long ownerId) {
        Project savedProject = projectRepository.save(
                projectMapper.toModel(requestDto, ownerId, userRepository));
        return projectMapper.toDto(savedProject);

    }

    @Override
    public ProjectResponseDto findById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with ID " + projectId + " does not exist"));
        checkProjectAccessPermission(projectId, userId);
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectResponseDto> findAll(Long userId, Pageable pageable) {
        return projectRepository.findDistinctByOwnerIdOrUsersId(userId, userId, pageable)
                .map(projectMapper::toDto);
    }

    @Override
    public void update(Long projectId, ProjectUpdateRequestDto updateRequestDto, Long ownerId) {
        checkProjectOwnerPermission(projectId, ownerId);
        Project project = projectRepository.getReferenceById(projectId);
        projectMapper.update(project, updateRequestDto, userRepository);
        projectRepository.save(project);
    }

    @Override
    public void delete(Long projectId, Long ownerId) {
        checkProjectOwnerPermission(projectId, ownerId);
        projectRepository.deleteById(projectId);
    }

    @Override
    public List<ProjectResponseDto> search(ProjectSearchParameters searchParameters, Long userId) {
        Specification<Project> specification = projectSpecificationBuilder
                .build(searchParameters);
        return projectRepository.findAll(specification)
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public void checkProjectOwnerPermission(Long projectId, Long ownerId) {
        if (!projectRepository.existsByIdAndOwnerId(projectId, ownerId)) {
            throw new AccessDeniedException(
                    "You do not have a permission to change this project."
                            + " Id: " + projectId);
        }
    }

    @Override
    public void checkProjectAccessPermission(Long projectId, Long userId) {
        if (!projectRepository.existsByIdAndUserIsMember(projectId, userId)) {
            throw new AccessDeniedException(
                    "You do not have a permission to interact with this project."
                            + " Id: " + projectId);
        }
    }
}
