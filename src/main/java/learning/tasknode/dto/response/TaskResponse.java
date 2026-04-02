package learning.tasknode.dto.response;

import learning.tasknode.enums.TaskPriority;
import learning.tasknode.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long projectId;
    private String projectName;

    private Long assigneeId;
    private String assigneeName;
    private String assigneeAvatarUrl;

    private Long createdById;
    private String createdByName;

    private Long parentTaskId;
    private String parentTaskTitle;

    private Set<TagResponse> tags;
    private List<TaskAttachmentResponse> attachments;
    private Integer commentCount;
    private Integer subTaskCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
