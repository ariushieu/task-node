package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotNull;
import learning.tasknode.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role in project is required")
    private ProjectRole roleInProject;
}
