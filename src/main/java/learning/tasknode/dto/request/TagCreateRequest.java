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
public class TagCreateRequest {

    @NotBlank(message = "Tag name is required")
    @Size(max = 50)
    private String name;

    @Size(max = 7, message = "Color must be a hex code (e.g. #FF5733)")
    private String color;
}
