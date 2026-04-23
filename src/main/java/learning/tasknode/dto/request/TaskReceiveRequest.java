package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskReceiveRequest {
    @NotNull(message = "UserId is required")
    private Long userId;
}