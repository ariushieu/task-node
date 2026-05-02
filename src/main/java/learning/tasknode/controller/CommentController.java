package learning.tasknode.controller;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.CommentRequest;
import learning.tasknode.dto.response.CommentResponse;
import learning.tasknode.entity.User;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.repository.UserRepository;
import learning.tasknode.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<CommentResponse>> getComments(@PathVariable Long taskId, org.springframework.data.domain.Pageable pageable) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(commentService.getCommentsOfTask(taskId, userId, pageable));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(taskId, request));
    }
}
