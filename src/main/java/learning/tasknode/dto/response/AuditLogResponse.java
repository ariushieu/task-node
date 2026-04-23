package learning.tasknode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {
    private Long id;
    private String userName;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime timestamp;
}
