package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotNull;
import learning.tasknode.enums.ApprovalAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "Action is required (APPROVE or REJECT)")
    private ApprovalAction action;

    private String reason;
}
