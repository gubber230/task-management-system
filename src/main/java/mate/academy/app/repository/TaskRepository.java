package mate.academy.app.repository;

import java.util.Optional;
import mate.academy.app.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long>,
        JpaSpecificationExecutor<Task> {
    Page<Task> findAllByAssigneeId(Long assigneeId, Pageable pageable);

    @Query("SELECT t.projectId FROM Task t WHERE t.id = :taskId")
    Optional<Long> findProjectIdById(@Param("taskId") Long taskId);
}
