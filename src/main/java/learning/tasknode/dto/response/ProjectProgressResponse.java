package learning.tasknode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectProgressResponse {
    private Long projectId;
    private String projectName;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int overdueTasks;
    private double percentCompleted;
    private double avgProgress;
}
