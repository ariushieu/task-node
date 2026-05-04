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

    @Query("SELECT DISTINCT ta.task FROM learning.tasknode.entity.TaskAssignee ta WHERE ta.user.id = :userId AND ta.task.isDeleted = false AND ta.task.startDate <= :end AND ta.task.endDate >= :start")
    Page<Task> findCalendarTasksByUser(Long userId, java.time.LocalDate start, java.time.LocalDate end, Pageable pageable);

    @Query(value = "SELECT t.project_id, COUNT(*), SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status IN ('IN_PROGRESS','IN_REVIEW','WAITING_APPROVAL','TODO','NEW') AND t.end_date < CURDATE() THEN 1 ELSE 0 END) FROM tasks t WHERE t.is_deleted = false GROUP BY t.project_id", nativeQuery = true)
    List<Object[]> getProjectProgressStats();

    @Query(value = "SELECT t.project_id, AVG(ta.progress) FROM task_assignees ta JOIN tasks t ON ta.task_id = t.id WHERE t.is_deleted = false GROUP BY t.project_id", nativeQuery = true)
    List<Object[]> getAvgProgressByProject();

    @Query(value = "SELECT ta.user_id, COUNT(*), SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status = 'REJECTED' THEN 1 ELSE 0 END), AVG(ta.progress) FROM task_assignees ta JOIN tasks t ON ta.task_id = t.id WHERE t.is_deleted = false GROUP BY ta.user_id", nativeQuery = true)
    List<Object[]> getEmployeePerformanceAll();

    @Query(value = "SELECT ta.user_id, COUNT(*), SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), SUM(CASE WHEN t.status = 'REJECTED' THEN 1 ELSE 0 END), AVG(ta.progress) FROM task_assignees ta JOIN tasks t ON ta.task_id = t.id WHERE t.is_deleted = false AND t.created_at >= :start AND t.created_at <= :end GROUP BY ta.user_id", nativeQuery = true)
    List<Object[]> getEmployeePerformanceFiltered(LocalDateTime start, LocalDateTime end);

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