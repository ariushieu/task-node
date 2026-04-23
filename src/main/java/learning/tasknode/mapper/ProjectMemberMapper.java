package learning.tasknode.mapper;

import learning.tasknode.dto.response.ProjectMemberResponse;
import learning.tasknode.entity.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "userAvatarUrl", source = "user.avatarUrl")
    ProjectMemberResponse toResponse(ProjectMember entity);
}
