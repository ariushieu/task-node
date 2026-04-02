package learning.tasknode.dto.response;

import learning.tasknode.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;
    private ProjectRole roleInProject;
    private LocalDateTime joinedAt;
}
