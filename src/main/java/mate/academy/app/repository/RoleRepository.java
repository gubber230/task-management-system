package mate.academy.app.repository;

import java.util.Optional;
import mate.academy.app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(Role.RoleName name);
}
