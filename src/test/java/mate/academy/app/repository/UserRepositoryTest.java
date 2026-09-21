package mate.academy.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import mate.academy.app.model.Role;
import mate.academy.app.model.User;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@DataJpaTest
@Sql(scripts = "/sql/user/insert-user-repository-data.sql",
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/user/cleanup-user-repository-data.sql",
        executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ExistingUsername_ReturnsUserWithRolesLoaded() {
        Optional<User> actual = userRepository.findByUsername("john_doe_rtest");

        assertThat(actual).isPresent();
        assertThat(actual.get().getEmail()).isEqualTo("john_rtest@example.com");
        assertThat(Hibernate.isInitialized(actual.get().getRoles())).isTrue();
        assertThat(actual.get().getRoles()).extracting(Role::getRole)
                .containsExactly(Role.RoleName.USER);
    }

    @Test
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        assertThat(userRepository.existsByEmail("john_rtest@example.com")).isTrue();
    }

    @Test
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        assertThat(userRepository.existsByEmail("unknown_rtest@example.com")).isFalse();
    }

    @Test
    void existsByUsername_ExistingUsername_ReturnsTrue() {
        assertThat(userRepository.existsByUsername("john_doe_rtest")).isTrue();
    }

    @Test
    void existsByUsername_NonExistingUsername_ReturnsFalse() {
        assertThat(userRepository.existsByUsername("unknown_rtest_user")).isFalse();
    }
}
