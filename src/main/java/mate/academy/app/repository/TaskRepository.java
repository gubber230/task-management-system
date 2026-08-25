package mate.academy.app.repository;

import java.util.Optional;
import mate.academy.app.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByAssigneeId(Long assigneeId, Pageable pageable);

    Optional<Long> findProjectIdById(Long taskId);
}
