package learning.tasknode.mapper;

import learning.tasknode.dto.response.TaskAttachmentResponse;
import learning.tasknode.entity.TaskAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskAttachmentMapper {
    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    @Mapping(target = "uploadedByName", source = "uploadedBy.fullName")
    TaskAttachmentResponse toResponse(TaskAttachment entity);
}
