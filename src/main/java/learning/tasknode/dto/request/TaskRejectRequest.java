package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRejectRequest {
    @NotBlank(message = "Lý do từ chối bắt buộc nhập!")
    private String reason;
}
