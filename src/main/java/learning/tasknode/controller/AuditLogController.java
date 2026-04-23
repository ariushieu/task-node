package learning.tasknode.controller;

import learning.tasknode.dto.response.AuditLogResponse;
import learning.tasknode.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(@PageableDefault Pageable pageable) {
        Page<AuditLogResponse> page = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(page);
    }
}
