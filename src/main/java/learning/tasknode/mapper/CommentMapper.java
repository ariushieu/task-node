package learning.tasknode.mapper;

import learning.tasknode.dto.response.CommentResponse;
import learning.tasknode.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    @Mapping(target = "userAvatarUrl", source = "user.avatarUrl")
    CommentResponse toResponse(Comment entity);
}
