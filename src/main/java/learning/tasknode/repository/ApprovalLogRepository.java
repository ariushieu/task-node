package learning.tasknode.repository;

import learning.tasknode.entity.ApprovalLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalLogRepository extends JpaRepository<ApprovalLog, Long> {
}
