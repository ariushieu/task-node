package learning.tasknode.dto.request;

import jakarta.validation.constraints.Size;
import learning.tasknode.enums.TaskPriority;
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
public class TaskUpdateRequest {

    @Size(max = 200)
    private String title;

    private String description;

    private TaskPriority priority;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long parentTaskId;

    private Set<Long> tagIds;
}
