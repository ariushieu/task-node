package learning.tasknode.mapper;

import learning.tasknode.dto.request.TaskCreateRequest;
import learning.tasknode.dto.request.TaskUpdateRequest;
import learning.tasknode.dto.response.TaskResponse;
import learning.tasknode.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TaskAttachmentMapper.class})
public interface TaskMapper {
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "parentTask", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Task toEntity(TaskCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "assignee", ignore = true)
    void updateEntityFromDto(TaskUpdateRequest dto, @MappingTarget Task entity);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "assigneeName", source = "assignee.fullName")
    @Mapping(target = "assigneeAvatarUrl", source = "assignee.avatarUrl")
    @Mapping(target = "createdById", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "parentTaskTitle", source = "parentTask.title")
    @Mapping(target = "commentCount", expression = "java(entity.getComments() != null ? entity.getComments().size() : 0)")
    @Mapping(target = "subTaskCount", expression = "java(entity.getSubTasks() != null ? entity.getSubTasks().size() : 0)")
    TaskResponse toResponse(Task entity);
}
