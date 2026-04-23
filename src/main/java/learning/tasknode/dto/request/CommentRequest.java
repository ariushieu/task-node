package learning.tasknode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequest {
    @NotNull(message = "ID người bình luận bắt buộc nhập!")
    private Long userId;
    @NotBlank(message = "Nội dung bình luận không được để trống!")
    private String content;
}
