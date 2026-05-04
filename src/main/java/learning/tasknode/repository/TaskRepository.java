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

    @Query("SELECT t.project.id, COUNT(t), SUM(CASE WHEN t.status='DONE' OR t.status='APPROVED' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status='IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status IN ('IN_PROGRESS','TODO','NEW') AND t.endDate < CURRENT_DATE THEN 1 ELSE 0 END) FROM Task t WHERE t.isDeleted = false GROUP BY t.project.id")
    List<Object[]> getProjectProgressStats();

    // Thống kê hiệu suất theo từng nhân viên trong khoảng thời gian
    @Query("SELECT ta.user.id, COUNT(ta), SUM(CASE WHEN ta.task.status='DONE' OR ta.task.status='APPROVED' THEN 1 ELSE 0 END), SUM(CASE WHEN ta.task.status='IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN ta.task.status='REJECTED' THEN 1 ELSE 0 END), AVG(ta.progress) FROM learning.tasknode.entity.TaskAssignee ta WHERE ta.task.isDeleted = false AND (:start IS NULL OR ta.task.createdAt >= :start) AND (:end IS NULL OR ta.task.createdAt <= :end) GROUP BY ta.user.id")
    List<Object[]> getEmployeePerformance(LocalDateTime start, LocalDateTime end);

    @Query("SELECT DISTINCT ta.task FROM learning.tasknode.entity.TaskAssignee ta WHERE ta.user.id = :userId AND ta.task.isDeleted = false")
    Page<Task> findByAssigneeUserId(Long userId, Pageable pageable);

    Page<Task> findByDepartmentIdAndIsDeletedFalse(Long departmentId, Pageable pageable);

    Page<Task> findByDepartmentIdInAndIsDeletedFalse(java.util.List<Long> departmentIds, Pageable pageable);

    long countByStatusAndIsDeletedFalse(learning.tasknode.enums.TaskStatus status);

    long countByIsDeletedFalse();

    @Query("SELECT DISTINCT ta.user FROM learning.tasknode.entity.TaskAssignee ta WHERE ta.task.project.id = :projectId AND ta.task.isDeleted = false")
    List<learning.tasknode.entity.User> findDistinctAssigneesByProjectId(Long projectId);

    @Query("SELECT COUNT(DISTINCT ta.user) FROM learning.tasknode.entity.TaskAssignee ta WHERE ta.task.project.id = :projectId AND ta.task.isDeleted = false")
    int countDistinctAssigneesByProjectId(Long projectId);
}