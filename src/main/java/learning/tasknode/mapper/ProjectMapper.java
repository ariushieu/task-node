package learning.tasknode.mapper;

import learning.tasknode.dto.request.ProjectCreateRequest;
import learning.tasknode.dto.request.ProjectUpdateRequest;
import learning.tasknode.dto.response.ProjectResponse;
import learning.tasknode.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ProjectMemberMapper.class, UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "owner", ignore = true)
    Project toEntity(ProjectCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProjectUpdateRequest dto, @MappingTarget Project entity);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", source = "owner.fullName")
    @Mapping(target = "memberCount", expression = "java(entity.getMembers() != null ? entity.getMembers().size() : 0)")
    @Mapping(target = "taskCount", expression = "java(entity.getTasks() != null ? entity.getTasks().size() : 0)")
    ProjectResponse toResponse(Project entity);
}
