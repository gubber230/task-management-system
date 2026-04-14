package mate.academy.app.repository;

import mate.academy.app.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT COUNT(p) > 0 "
            + "FROM Project p "
            + "LEFT JOIN p.users u "
            + "WHERE p.id = :projectId "
            + "AND (p.owner.id = :userId OR u.id = :userId)")
    boolean isProjectMember(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT p "
            + "FROM Project p "
            + "LEFT JOIN p.users u "
            + "WHERE p.owner.id = :userId OR u.id = :userId")
    Page<Project> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 "
            + "FROM Project p "
            + "WHERE p.id = :projectId "
            + "AND p.owner.id = :ownerId")
    boolean isProjectOwner(@Param("projectId") Long projectId, @Param("ownerId") Long ownerId);
}
