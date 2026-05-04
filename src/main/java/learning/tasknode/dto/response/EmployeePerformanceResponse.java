package learning.tasknode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePerformanceResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String departmentName;
    private int totalTasks;
    private int completedTasks;
    private int inProgressTasks;
    private int rejectedTasks;
    private double avgProgress;
}
