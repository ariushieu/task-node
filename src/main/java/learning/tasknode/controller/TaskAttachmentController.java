package learning.tasknode.controller;

import jakarta.servlet.http.HttpServletRequest;
import learning.tasknode.dto.response.TaskAttachmentResponse;
import learning.tasknode.service.TaskAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskAttachmentController {
    private final TaskAttachmentService attachmentService;

    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<Page<TaskAttachmentResponse>> listTaskAttachments(@PathVariable Long taskId, org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(attachmentService.listByTask(taskId, pageable));
    }

    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TaskAttachmentResponse> uploadAttachment(
            @PathVariable Long taskId,
            @RequestPart("file") MultipartFile file,
            HttpServletRequest req) throws IOException {
        // Đơn giản: lấy tên user từ header (hoặc security context)
        String uploader = req.getHeader("X-User") != null ? req.getHeader("X-User") : "system";
        return ResponseEntity.ok(attachmentService.uploadAttachment(taskId, file, uploader));
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) throws IOException {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}
