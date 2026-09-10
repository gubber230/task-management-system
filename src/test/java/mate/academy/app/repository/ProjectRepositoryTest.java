package mate.academy.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import mate.academy.app.model.Project;
import mate.academy.app.model.User;
import mate.academy.app.model.enums.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private User member;
    private User outsider;
    private Project project;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setUsername("owner");
        owner.setEmail("owner@example.com");
        owner.setPassword("password");
        entityManager.persist(owner);

        member = new User();
        member.setUsername("member");
        member.setEmail("member@example.com");
        member.setPassword("password");
        entityManager.persist(member);

        outsider = new User();
        outsider.setUsername("outsider");
        outsider.setEmail("outsider@example.com");
        outsider.setPassword("password");
        entityManager.persist(outsider);

        project = new Project();
        project.setName("App Development");
        project.setDescription("Main project");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(30));
        project.setStatus(ProjectStatus.INITIATED);
        project.setOwnerId(owner.getId());
        project.setUsers(new HashSet<>(Set.of(member)));
        entityManager.persist(project);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void existsByIdAndOwnerId_MatchingOwner_ReturnsTrue() {
        boolean exists = projectRepository.existsByIdAndOwnerId(project.getId(), owner.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdAndOwnerId_MismatchedOwner_ReturnsFalse() {
        boolean exists = projectRepository.existsByIdAndOwnerId(project.getId(), outsider.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void existsByIdAndUserIsMember_UserIsOwner_ReturnsTrue() {
        boolean isMember = projectRepository.existsByIdAndUserIsMember(project.getId(), owner.getId());
        assertThat(isMember).isTrue();
    }

    @Test
    void existsByIdAndUserIsMember_UserIsCollaborator_ReturnsTrue() {
        boolean isMember = projectRepository.existsByIdAndUserIsMember(project.getId(), member.getId());
        assertThat(isMember).isTrue();
    }

    @Test
    void existsByIdAndUserIsMember_UserNotAssociated_ReturnsFalse() {
        boolean isMember = projectRepository.existsByIdAndUserIsMember(project.getId(), outsider.getId());
        assertThat(isMember).isFalse();
    }

    @Test
    void findDistinctByOwnerIdOrUsersId_UserIsOwnerAndMember_ReturnsUniqueProject() {
        Project p = entityManager.find(Project.class, project.getId());
        p.getUsers().add(entityManager.find(User.class, owner.getId()));
        entityManager.flush();
        entityManager.clear();

        Page<Project> result = projectRepository.findDistinctByOwnerIdOrUsersId(
                owner.getId(), owner.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(project.getId());
    }
}
