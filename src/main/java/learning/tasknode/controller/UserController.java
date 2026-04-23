package learning.tasknode.controller;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.UserCreateRequest;
import learning.tasknode.dto.response.UserResponse;
import learning.tasknode.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<UserResponse>> getAllUsers(org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody learning.tasknode.dto.request.UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
