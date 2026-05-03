package learning.tasknode.service;

import learning.tasknode.dto.response.DashboardStatsResponse;
import learning.tasknode.enums.TaskStatus;
import learning.tasknode.repository.ProjectRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardStatsResponse getStats() {
        long totalUsers = userRepository.countByIsDeletedFalse();
        long totalProjects = projectRepository.countByIsDeletedFalse();
        long totalTasks = taskRepository.countByIsDeletedFalse();
        long pendingApproval = taskRepository.countByStatusAndIsDeletedFalse(TaskStatus.WAITING_APPROVAL);

        Map<String, Long> tasksByStatus = new LinkedHashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            tasksByStatus.put(status.name(), taskRepository.countByStatusAndIsDeletedFalse(status));
        }

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalProjects(totalProjects)
                .totalTasks(totalTasks)
                .pendingApprovalCount(pendingApproval)
                .tasksByStatus(tasksByStatus)
                .build();
    }
}
