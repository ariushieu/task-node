package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignRequest {

    @NotNull(message = "Assignee IDs are required")
    private java.util.List<Long> assigneeIds;
}
