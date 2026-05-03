package learning.tasknode.repository;
import learning.tasknode.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);

    @Query("SELECT d FROM Department d WHERE d.isDeleted = false")
    Page<Department> findAllActive(Pageable pageable);

    @Query("SELECT d FROM Department d WHERE d.id = :id AND d.isDeleted = false")
    java.util.Optional<Department> findByIdAndIsDeletedFalse(Long id);

    java.util.List<Department> findByManagerIdAndIsDeletedFalse(Long managerId);
}
