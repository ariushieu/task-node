package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private learning.tasknode.enums.TaskStatus status;
}