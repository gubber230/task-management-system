package mate.academy.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;
import mate.academy.app.model.Role;
import mate.academy.app.model.User;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRole(Role.RoleName.USER);
        entityManager.persist(role);

        user = new User();
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("secret123");
        user.setRoles(Set.of(role));
        entityManager.persist(user);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("findByUsername eagerly loads roles via entity graph")
    void findByUsername_ExistingUsername_ReturnsUserWithRolesLoaded() {
        Optional<User> actual = userRepository.findByUsername("john_doe");

        assertThat(actual).isPresent();
        assertThat(actual.get().getEmail()).isEqualTo("john@example.com");
        assertThat(Hibernate.isInitialized(actual.get().getRoles())).isTrue();
        assertThat(actual.get().getRoles()).extracting(Role::getRole)
                .containsExactly(Role.RoleName.USER);
    }

    @Test
    @DisplayName("existsByEmail returns true for registered email")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        boolean exists = userRepository.existsByEmail("john@example.com");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail returns false for unregistered email")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        boolean exists = userRepository.existsByEmail("unknown@example.com");
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByUsername returns true for registered username")
    void existsByUsername_ExistingUsername_ReturnsTrue() {
        boolean exists = userRepository.existsByUsername("john_doe");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername returns false for unregistered username")
    void existsByUsername_NonExistingUsername_ReturnsFalse() {
        boolean exists = userRepository.existsByUsername("unknown_user");
        assertThat(exists).isFalse();
    }
}
