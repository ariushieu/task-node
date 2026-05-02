package learning.tasknode.repository;

import learning.tasknode.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("SELECT a FROM AuditLog a WHERE a.isDeleted = false ORDER BY a.createdAt DESC")
    Page<AuditLog> findAllActive(Pageable pageable);
}
