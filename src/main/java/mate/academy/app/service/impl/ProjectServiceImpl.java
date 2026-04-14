package mate.academy.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.external.ProjectRequestDto;
import mate.academy.app.dto.external.ProjectResponseDto;
import mate.academy.app.dto.external.ProjectUpdateRequestDto;
import mate.academy.app.mapper.ProjectMapper;
import mate.academy.app.model.Project;
import mate.academy.app.repository.ProjectRepository;
import mate.academy.app.repository.UserRepository;
import mate.academy.app.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public ProjectResponseDto create(ProjectRequestDto requestDto, Long ownerId) {
        Project savedProject = projectRepository.save(
                projectMapper.toModel(requestDto, ownerId, userRepository));
        return projectMapper.toDto(savedProject);

    }

    @Override
    public ProjectResponseDto findById(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project with Id " + projectId + " do not exist"));
        checkAccessPermission(projectId, userId);
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectResponseDto> findAll(Long userId, Pageable pageable) {
        return projectRepository.findAllByUserId(userId, pageable)
                .map(projectMapper::toDto);
    }

    @Override
    public void update(Long projectId, ProjectUpdateRequestDto updateRequestDto, Long ownerId) {
        checkOwnerPermission(projectId, ownerId);
        Project project = projectRepository.getReferenceById(projectId);
        projectMapper.update(project, updateRequestDto, userRepository);
        projectRepository.save(project);
    }

    @Override
    public void delete(Long projectId, Long ownerId) {
        checkOwnerPermission(projectId, ownerId);
        projectRepository.deleteById(projectId);
    }

    private void checkAccessPermission(Long projectId, Long userId) {
        if (!projectRepository.isProjectMember(projectId, userId)) {
            throw new AccessDeniedException(
                    "You do not have a permission to interact with this project."
                            + " Id: " + projectId);
        }
    }

    private void checkOwnerPermission(Long projectId, Long ownerId) {
        if (!projectRepository.isProjectOwner(projectId, ownerId)) {
            throw new AccessDeniedException(
                    "You do not have a permission to change this project."
                            + " Id: " + projectId);
        }
    }
}
