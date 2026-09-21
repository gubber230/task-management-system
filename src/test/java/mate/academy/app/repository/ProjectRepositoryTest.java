package mate.academy.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import mate.academy.app.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@Sql(scripts = "/sql/project/insert-project-repository-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/project/cleanup-project-repository-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class ProjectRepositoryTest {

    private final Long projectId = 101L;
    private final Long ownerId = 101L;
    private final Long memberId = 102L;
    private final Long outsiderId = 103L;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void existsByIdAndOwnerId_MatchingOwner_ReturnsTrue() {
        assertThat(projectRepository.existsByIdAndOwnerId(projectId, ownerId)).isTrue();
    }

    @Test
    void existsByIdAndOwnerId_MismatchedOwner_ReturnsFalse() {
        assertThat(projectRepository.existsByIdAndOwnerId(projectId, outsiderId)).isFalse();
    }

    @Test
    void existsByIdAndUserIsMember_UserIsOwner_ReturnsTrue() {
        assertThat(projectRepository.existsByIdAndUserIsMember(projectId, ownerId)).isTrue();
    }

    @Test
    void existsByIdAndUserIsMember_UserIsCollaborator_ReturnsTrue() {
        assertThat(projectRepository.existsByIdAndUserIsMember(projectId, memberId)).isTrue();
    }

    @Test
    void existsByIdAndUserIsMember_UserNotAssociated_ReturnsFalse() {
        assertThat(projectRepository.existsByIdAndUserIsMember(projectId, outsiderId)).isFalse();
    }

    @Test
    void findDistinctByOwnerIdOrUsersId_UserIsOwnerAndMember_ReturnsUniqueProject() {
        Page<Project> result = projectRepository.findDistinctByOwnerIdOrUsersId(
                ownerId, ownerId, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(projectId);
    }
}
