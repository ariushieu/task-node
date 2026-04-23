package learning.tasknode.mapper;

import learning.tasknode.dto.request.UserCreateRequest;
import learning.tasknode.dto.request.UserUpdateRequest;
import learning.tasknode.dto.response.UserResponse;
import learning.tasknode.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    User toEntity(UserCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserUpdateRequest dto, @MappingTarget User entity);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    UserResponse toResponse(User entity);
}