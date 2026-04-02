package learning.tasknode.dto.response;

import learning.tasknode.enums.ApprovalAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLogResponse {

    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long approverId;
    private String approverName;
    private ApprovalAction action;
    private String reason;
    private LocalDateTime createdAt;
}
