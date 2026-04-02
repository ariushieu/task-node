package learning.tasknode.dto.response;

import learning.tasknode.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private ProjectStatus status;
    private Long ownerId;
    private String ownerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer memberCount;
    private Integer taskCount;
    private List<ProjectMemberResponse> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
