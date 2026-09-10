package mate.academy.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import mate.academy.app.model.Project;
import mate.academy.app.model.Task;
import mate.academy.app.model.User;
import mate.academy.app.model.enums.ProjectStatus;
import mate.academy.app.model.enums.TaskPriority;
import mate.academy.app.model.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User assignee;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        assignee = new User();
        assignee.setUsername("developer");
        assignee.setEmail("dev@example.com");
        assignee.setPassword("password");
        entityManager.persist(assignee);

        project = new Project();
        project.setName("Core API");
        project.setDescription("Backend");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusDays(10));
        project.setStatus(ProjectStatus.INITIATED);
        project.setOwnerId(assignee.getId());
        entityManager.persist(project);

        task = new Task();
        task.setName("Create endpoints");
        task.setDescription("Write controllers");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setDueDate(LocalDate.now().plusDays(5));
        task.setProjectId(project.getId());
        task.setAssigneeId(assignee.getId());
        entityManager.persist(task);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findAllByAssigneeId returns page of tasks for given user")
    void findAllByAssigneeId_ValidAssignee_ReturnsPagedTasks() {
        Page<Task> tasks = taskRepository.findAllByAssigneeId(assignee.getId(), PageRequest.of(0, 10));

        assertThat(tasks.getTotalElements()).isEqualTo(1);
        assertThat(tasks.getContent().getFirst().getName()).isEqualTo("Create endpoints");
    }

    @Test
    @DisplayName("findAllByAssigneeId returns empty page when no tasks assigned")
    void findAllByAssigneeId_NoAssignedTasks_ReturnsEmptyPage() {
        Page<Task> tasks = taskRepository.findAllByAssigneeId(999L, PageRequest.of(0, 10));

        assertThat(tasks.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findProjectIdById returns project ID for valid task")
    void findProjectIdById_ExistingTask_ReturnsProjectId() {
        Optional<Long> actualProjectId = taskRepository.findProjectIdById(task.getId());

        assertThat(actualProjectId).isPresent();
        assertThat(actualProjectId.get()).isEqualTo(project.getId());
    }

    @Test
    @DisplayName("findProjectIdById returns empty optional for non-existing task")
    void findProjectIdById_NonExistingTask_ReturnsEmpty() {
        Optional<Long> actualProjectId = taskRepository.findProjectIdById(999L);

        assertThat(actualProjectId).isEmpty();
    }
}
