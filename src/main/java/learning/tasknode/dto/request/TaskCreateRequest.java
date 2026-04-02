package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import learning.tasknode.enums.TaskPriority;
import learning.tasknode.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long assigneeId;

    private Long parentTaskId;

    private Set<Long> tagIds;
}
