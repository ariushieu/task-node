package learning.tasknode.repository;

import learning.tasknode.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.isDeleted = false")
    Page<Task> findAllActive(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.isDeleted = false")
    Optional<Task> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT t FROM Task t WHERE t.isDeleted = false AND t.startDate >= :start AND t.endDate <= :end")
    Page<Task> findByCalendarRange(java.time.LocalDate start, java.time.LocalDate end, Pageable pageable);

    // Thống kê tiến độ dự án
    @Query("SELECT t.project.id, COUNT(t), SUM(CASE WHEN t.status='DONE' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status='IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status IN ('IN_PROGRESS','TODO','NEW') AND t.endDate < CURRENT_DATE THEN 1 ELSE 0 END) FROM Task t WHERE t.isDeleted = false GROUP BY t.project.id")
    List<Object[]> getProjectProgressStats();

    // Thống kê hiệu suất theo từng nhân viên trong khoảng thời gian
    @Query("SELECT t.assignee.id, COUNT(t), SUM(CASE WHEN t.status='DONE' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status='IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status='REJECTED' THEN 1 ELSE 0 END) FROM Task t WHERE t.assignee IS NOT NULL AND t.isDeleted = false AND (:start IS NULL OR t.createdAt >= :start) AND (:end IS NULL OR t.createdAt <= :end) GROUP BY t.assignee.id")
    List<Object[]> getEmployeePerformance(LocalDateTime start, LocalDateTime end);
}