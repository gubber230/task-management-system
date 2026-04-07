package mate.academy.app.repository;

import java.util.Optional;
import mate.academy.app.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsername(String username);
}
