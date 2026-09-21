package mate.academy.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import mate.academy.app.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@Sql(scripts = "/sql/task/insert-task-repository-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/task/cleanup-task-repository-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class TaskRepositoryTest {

    private final Long taskId = 111L;
    private final Long assigneeId = 111L;
    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findAllByAssigneeId_ValidAssignee_ReturnsPagedTasks() {
        Page<Task> tasks = taskRepository.findAllByAssigneeId(assigneeId, PageRequest.of(0, 10));

        assertThat(tasks.getTotalElements()).isEqualTo(1);
        assertThat(tasks.getContent().getFirst().getName()).isEqualTo("Create endpoints");
    }

    @Test
    void findAllByAssigneeId_NoAssignedTasks_ReturnsEmptyPage() {
        Page<Task> tasks = taskRepository.findAllByAssigneeId(999_999L, PageRequest.of(0, 10));

        assertThat(tasks.getContent()).isEmpty();
    }

    @Test
    void findProjectIdById_ExistingTask_ReturnsProjectId() {
        Optional<Long> actual = taskRepository.findProjectIdById(taskId);

        assertThat(actual).contains(111L);
    }

    @Test
    void findProjectIdById_NonExistingTask_ReturnsEmpty() {
        assertThat(taskRepository.findProjectIdById(999_999L)).isEmpty();
    }
}
