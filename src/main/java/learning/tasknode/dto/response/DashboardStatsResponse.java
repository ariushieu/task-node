package learning.tasknode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalProjects;
    private long totalTasks;
    private long pendingApprovalCount;
    private Map<String, Long> tasksByStatus;
}
