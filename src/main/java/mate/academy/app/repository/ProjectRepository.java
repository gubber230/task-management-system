package mate.academy.app.repository;

import mate.academy.app.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long>,
        JpaSpecificationExecutor<Project> {
    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT COUNT(p) > 0 "
            + "FROM Project p "
            + "LEFT JOIN p.users u "
            + "WHERE p.id = :projectId "
            + "AND (p.ownerId = :userId OR u.id = :userId)")
    boolean existsByIdAndUserIsMember(@Param("projectId") Long projectId,
                                      @Param("userId") Long userId);

    Page<Project> findDistinctByOwnerIdOrUsersId(Long ownerId, Long userId, Pageable pageable);

}
