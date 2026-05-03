package learning.tasknode.repository;

import learning.tasknode.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.isDeleted = false")
    Page<Project> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.isDeleted = false")
    java.util.Optional<Project> findByIdAndIsDeletedFalse(Long id);

    boolean existsByNameAndIsDeletedFalse(String name);

    long countByIsDeletedFalse();
}
