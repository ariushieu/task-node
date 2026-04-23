package learning.tasknode.service;

import learning.tasknode.dto.response.AuditLogResponse;
import learning.tasknode.entity.AuditLog;
import learning.tasknode.mapper.AuditLogMapper;
import learning.tasknode.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(auditLogMapper::toResponse);
    }
}
