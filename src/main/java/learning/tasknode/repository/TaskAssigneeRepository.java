package learning.tasknode.repository;

import learning.tasknode.entity.TaskAssignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, Long> {
    Optional<TaskAssignee> findByTaskIdAndUserId(Long taskId, Long userId);
}
