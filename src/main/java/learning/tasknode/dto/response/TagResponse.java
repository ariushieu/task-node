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
public class TagResponse {

    private Long id;
    private String name;
    private String color;
    private LocalDateTime createdAt;
}
