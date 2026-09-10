package mate.academy.app.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.app.dto.internal.ProjectSearchParameters;
import mate.academy.app.dto.request.ProjectCreateRequestDto;
import mate.academy.app.dto.request.ProjectUpdateRequestDto;
import mate.academy.app.dto.response.ProjectResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.mapper.ProjectMapper;
import mate.academy.app.model.Project;
import mate.academy.app.model.enums.ProjectStatus;
import mate.academy.app.repository.ProjectRepository;
import mate.academy.app.repository.UserRepository;
import mate.academy.app.repository.filter.project.ProjectSpecificationBuilder;
import mate.academy.app.service.impl.ProjectServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectSpecificationBuilder projectSpecificationBuilder;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectResponseDto responseDto;
    private final Long projectId = 1L;
    private final Long userId = 2L;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(projectId);
        project.setOwnerId(userId);

        responseDto = new ProjectResponseDto(
                projectId, userId, "Name", "Description",
                LocalDate.now(), LocalDate.now().plusDays(1),
                ProjectStatus.INITIATED, Set.of(userId)
        );
    }

    @Test
    void create_ValidRequestDto_ReturnsProjectResponseDto() {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "Name", "Description", LocalDate.now().plusDays(1), Set.of(userId));

        when(projectMapper.toModel(requestDto, userId, userRepository)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        ProjectResponseDto actual = projectService.create(requestDto, userId);

        assertEquals(responseDto, actual);
        verify(projectRepository).save(project);
    }

    @Test
    void findById_ValidIdAndUserHasAccess_ReturnsProjectResponseDto() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.existsByIdAndUserIsMember(projectId, userId)).thenReturn(true);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        ProjectResponseDto actual = projectService.findById(projectId, userId);

        assertEquals(responseDto, actual);
    }

    @Test
    void findById_NotValidId_ThrowsEntityNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> projectService.findById(projectId, userId));
    }

    @Test
    void findById_UserLacksAccess_ThrowsAccessDeniedException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.existsByIdAndUserIsMember(projectId, userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> projectService.findById(projectId, userId));
    }

    @Test
    void findAll_ValidUserIdAndPageable_ReturnsPageOfProjectResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(List.of(project));

        when(projectRepository.findDistinctByOwnerIdOrUsersId(userId, userId, pageable))
                .thenReturn(projectPage);
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        Page<ProjectResponseDto> actual = projectService.findAll(userId, pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(responseDto, actual.getContent().getFirst());
    }

    @Test
    void update_ValidRequestAndOwner_Success() {
        ProjectUpdateRequestDto updateRequestDto = new ProjectUpdateRequestDto(
                "Updated", "Updated Desc", LocalDate.now().plusDays(2),
                ProjectStatus.IN_PROGRESS, Set.of(userId));

        when(projectRepository.existsByIdAndOwnerId(projectId, userId)).thenReturn(true);
        when(projectRepository.getReferenceById(projectId)).thenReturn(project);

        assertDoesNotThrow(() -> projectService.update(projectId, updateRequestDto, userId));

        verify(projectMapper).update(project, updateRequestDto, userRepository);
        verify(projectRepository).save(project);
    }

    @Test
    void update_UserLacksOwnerPermission_ThrowsAccessDeniedException() {
        ProjectUpdateRequestDto updateRequestDto = new ProjectUpdateRequestDto(
                "Updated", "Updated Desc", LocalDate.now().plusDays(2),
                ProjectStatus.IN_PROGRESS, Set.of(userId));

        when(projectRepository.existsByIdAndOwnerId(projectId, userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> projectService.update(projectId, updateRequestDto, userId));
    }

    @Test
    void delete_ValidIdAndOwner_Success() {
        when(projectRepository.existsByIdAndOwnerId(projectId, userId)).thenReturn(true);

        assertDoesNotThrow(() -> projectService.delete(projectId, userId));

        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void delete_UserLacksOwnerPermission_ThrowsAccessDeniedException() {
        when(projectRepository.existsByIdAndOwnerId(projectId, userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> projectService.delete(projectId, userId));
    }

    @Test
    void search_ValidParameters_ReturnsListOfProjectResponseDto() {
        ProjectSearchParameters searchParams = new ProjectSearchParameters();
        searchParams.setNames(new String[]{"Test Project"});
        searchParams.setUserId(userId);
        searchParams.setStatuses(new ProjectStatus[]{ProjectStatus.IN_PROGRESS});
        searchParams.setStartDate(LocalDate.now());
        searchParams.setEndDate(LocalDate.now().plusDays(10));
        Specification<Project> spec = (root, query, cb) -> null;

        when(projectSpecificationBuilder.build(searchParams)).thenReturn(spec);
        when(projectRepository.findAll(any(Specification.class))).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(responseDto);

        List<ProjectResponseDto> actual = projectService.search(searchParams, userId);

        assertEquals(1, actual.size());
        assertEquals(responseDto, actual.getFirst());
    }

    @Test
    void checkProjectOwnerPermission_ValidOwner_DoesNotThrow() {
        when(projectRepository.existsByIdAndOwnerId(projectId, userId)).thenReturn(true);

        assertDoesNotThrow(() -> projectService.checkProjectOwnerPermission(projectId, userId));
    }

    @Test
    void checkProjectOwnerPermission_InvalidOwner_ThrowsAccessDeniedException() {
        when(projectRepository.existsByIdAndOwnerId(projectId, userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> projectService.checkProjectOwnerPermission(projectId, userId));
    }

    @Test
    void checkProjectAccessPermission_ValidAccess_DoesNotThrow() {
        when(projectRepository.existsByIdAndUserIsMember(projectId, userId)).thenReturn(true);

        assertDoesNotThrow(() -> projectService.checkProjectAccessPermission(projectId, userId));
    }

    @Test
    void checkProjectAccessPermission_InvalidAccess_ThrowsAccessDeniedException() {
        when(projectRepository.existsByIdAndUserIsMember(projectId, userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> projectService.checkProjectAccessPermission(projectId, userId));
    }

    @Test
    void create_UserFromSetNotFound_ThrowsEntityNotFoundException() {
        ProjectCreateRequestDto requestDto = new ProjectCreateRequestDto(
                "Name", "Description", LocalDate.now().plusDays(1), Set.of(userId));

        when(projectMapper.toModel(requestDto, userId, userRepository))
                .thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class,
                () -> projectService.create(requestDto, userId));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void search_NoMatchingProjects_ReturnsEmptyList() {
        ProjectSearchParameters searchParams = new ProjectSearchParameters();
        Specification<Project> spec = (root, query, cb) -> null;

        when(projectSpecificationBuilder.build(searchParams)).thenReturn(spec);
        when(projectRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<ProjectResponseDto> actual = projectService.search(searchParams, userId);

        assertEquals(0, actual.size());
        verifyNoInteractions(projectMapper);
    }
}
