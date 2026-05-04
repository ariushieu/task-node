package learning.tasknode.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskProgressUpdateRequest {

    @NotNull(message = "Progress is required")
    @Min(value = 0, message = "Progress must be >= 0")
    @Max(value = 100, message = "Progress must be <= 100")
    private Integer progress;
}
