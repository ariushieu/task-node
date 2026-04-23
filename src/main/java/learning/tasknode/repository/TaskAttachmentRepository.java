package learning.tasknode.repository;

import learning.tasknode.entity.TaskAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {
    org.springframework.data.domain.Page<TaskAttachment> findByTaskIdAndIsDeletedFalse(Long taskId, org.springframework.data.domain.Pageable pageable);
}