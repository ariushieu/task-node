package learning.tasknode.controller;

import learning.tasknode.dto.response.NotificationResponse;
import learning.tasknode.entity.User;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.repository.UserRepository;
import learning.tasknode.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<NotificationResponse>> getUnreadNotifications(org.springframework.data.domain.Pageable pageable) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId, pageable));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
