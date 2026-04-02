package learning.tasknode.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import learning.tasknode.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(max = 100)
    private String fullName;

    @Email(message = "Email must be valid")
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    private String avatarUrl;

    private UserRole role;

    private Long departmentId;

    private Boolean isActive;
}
