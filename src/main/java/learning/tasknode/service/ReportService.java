package learning.tasknode.service;

import learning.tasknode.dto.response.EmployeePerformanceResponse;
import learning.tasknode.dto.response.ProjectProgressResponse;
import learning.tasknode.entity.Project;
import learning.tasknode.entity.User;
import learning.tasknode.repository.ProjectRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public org.springframework.data.domain.Page<ProjectProgressResponse> getProjectProgress(org.springframework.data.domain.Pageable pageable) {
        List<Object[]> stats = taskRepository.getProjectProgressStats();
        java.util.Map<Long, Double> avgProgressMap = new java.util.HashMap<>();
        for (Object[] row : taskRepository.getAvgProgressByProject()) {
            Long pid = ((Number) row[0]).longValue();
            Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            avgProgressMap.put(pid, avg);
        }
        List<ProjectProgressResponse> list = new ArrayList<>();
        for (Object[] row : stats) {
            Long projectId = ((Number) row[0]).longValue();
            Long total = ((Number) row[1]).longValue();
            Long done = ((Number) row[2]).longValue();
            Long inProgress = ((Number) row[3]).longValue();
            Long overdue = ((Number) row[4]).longValue();
            Project p = projectRepository.findById(projectId).orElse(null);
            if (p != null) {
                double percent = (total != 0) ? (done * 100.0 / total) : 0.0;
                list.add(ProjectProgressResponse.builder()
                        .projectId(projectId)
                        .projectName(p.getName())
                        .totalTasks(total.intValue())
                        .completedTasks(done.intValue())
                        .inProgressTasks(inProgress.intValue())
                        .overdueTasks(overdue.intValue())
                        .percentCompleted(percent)
                        .avgProgress(avgProgressMap.getOrDefault(projectId, 0.0))
                        .build());
            }
        }
        int startIdx = Math.min((int) pageable.getOffset(), list.size());
int endIdx = Math.min((startIdx + pageable.getPageSize()), list.size());
return new org.springframework.data.domain.PageImpl<>(list.subList(startIdx, endIdx), pageable, list.size());
    }

    public org.springframework.data.domain.Page<EmployeePerformanceResponse> getEmployeePerformance(String start, String end, org.springframework.data.domain.Pageable pageable) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        try { if (start != null) startTime = LocalDateTime.parse(start); } catch(Exception ignore) {}
        try { if (end != null) endTime = LocalDateTime.parse(end); } catch(Exception ignore) {}
        List<Object[]> stats = (startTime != null && endTime != null)
                ? taskRepository.getEmployeePerformanceFiltered(startTime, endTime)
                : taskRepository.getEmployeePerformanceAll();
        List<EmployeePerformanceResponse> result = new ArrayList<>();
        for (Object[] row : stats) {
            Long userId = ((Number) row[0]).longValue();
            Long total = ((Number) row[1]).longValue();
            Long done = ((Number) row[2]).longValue();
            Long inProgress = ((Number) row[3]).longValue();
            Long rejected = ((Number) row[4]).longValue();
            Double avgProg = row[5] != null ? ((Number) row[5]).doubleValue() : 0.0;
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                result.add(EmployeePerformanceResponse.builder()
                        .userId(userId)
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .departmentName(user.getDepartment() != null ? user.getDepartment().getName() : null)
                        .totalTasks(total.intValue())
                        .completedTasks(done.intValue())
                        .inProgressTasks(inProgress.intValue())
                        .rejectedTasks(rejected.intValue())
                        .avgProgress(avgProg != null ? avgProg : 0.0)
                        .build());
            }
        }
        int startIdx = Math.min((int) pageable.getOffset(), result.size());
int endIdx = Math.min((startIdx + pageable.getPageSize()), result.size());
return new org.springframework.data.domain.PageImpl<>(result.subList(startIdx, endIdx), pageable, result.size());
    }
}
