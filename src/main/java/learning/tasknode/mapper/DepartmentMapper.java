package learning.tasknode.mapper;

import learning.tasknode.dto.request.DepartmentCreateRequest;
import learning.tasknode.dto.response.DepartmentResponse;
import learning.tasknode.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {
    Department toEntity(DepartmentCreateRequest dto);

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", source = "manager.fullName")
    @Mapping(target = "memberCount", expression = "java(entity.getMembers() != null ? (int) entity.getMembers().stream().filter(m -> !Boolean.TRUE.equals(m.getIsDeleted())).count() : 0)")
    DepartmentResponse toResponse(Department entity);
}
