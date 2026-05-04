package learning.tasknode.repository;

import learning.tasknode.entity.TaskProgressLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskProgressLogRepository extends JpaRepository<TaskProgressLog, Long> {

    Optional<TaskProgressLog> findByTaskIdAndUserIdAndLogDate(Long taskId, Long userId, LocalDate logDate);

    List<TaskProgressLog> findByTaskIdAndUserIdAndLogDateBetweenOrderByLogDateAsc(Long taskId, Long userId, LocalDate start, LocalDate end);

    Optional<TaskProgressLog> findTopByTaskIdAndUserIdAndLogDateLessThanEqualOrderByLogDateDesc(Long taskId, Long userId, LocalDate date);
}
