package learning.tasknode.mapper;

import learning.tasknode.dto.request.DepartmentCreateRequest;
import learning.tasknode.dto.response.DepartmentResponse;
import learning.tasknode.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentCreateRequest dto);

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", source = "manager.fullName")
    @Mapping(target = "memberCount", expression = "java(entity.getMembers() != null ? entity.getMembers().size() : 0)")
    DepartmentResponse toResponse(Department entity);
}
