package mate.academy.app.repository;

import java.util.Optional;
import mate.academy.app.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByTaskId(Long taskId);
}
