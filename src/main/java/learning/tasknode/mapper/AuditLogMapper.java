package learning.tasknode.mapper;

import learning.tasknode.dto.response.AuditLogResponse;
import learning.tasknode.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "timestamp", source = "createdAt")
    AuditLogResponse toResponse(AuditLog auditLog);
}
