package learning.tasknode.controller;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.CommentRequest;
import learning.tasknode.dto.response.CommentResponse;
import learning.tasknode.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CommentResponse>> getComments(@PathVariable Long taskId, @RequestParam Long userId, org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsOfTask(taskId, userId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }
}
