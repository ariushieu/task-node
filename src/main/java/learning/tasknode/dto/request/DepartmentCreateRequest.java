package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentCreateRequest {

    @NotBlank(message = "Department name is required")
    @Size(max = 100)
    private String name;

    private String description;

    private Long managerId;
}
