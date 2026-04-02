package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import learning.tasknode.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 150)
    private String name;

    private String description;

    @NotNull(message = "Project status is required")
    private ProjectStatus status;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    private LocalDate startDate;

    private LocalDate endDate;
}
