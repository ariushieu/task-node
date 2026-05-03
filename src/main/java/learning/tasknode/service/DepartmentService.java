package learning.tasknode.service;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.DepartmentCreateRequest;
import learning.tasknode.dto.request.DepartmentUpdateRequest;
import learning.tasknode.dto.response.DepartmentResponse;
import learning.tasknode.entity.Department;
import learning.tasknode.entity.User;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.mapper.DepartmentMapper;
import learning.tasknode.repository.DepartmentRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentResponse createDepartment(@Valid DepartmentCreateRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Department name already exists");
        }
        Department department = departmentMapper.toEntity(request);
        if (request.getManagerId() != null) {
            User manager = userRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            department.setManager(manager);
        }
        Department saved = departmentRepository.save(department);
        return toResponseWithMemberCount(saved);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllActive(pageable)
            .map(this::toResponseWithMemberCount);
    }

    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department department = departmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        if (request.getName() != null) department.setName(request.getName());
        if (request.getDescription() != null) department.setDescription(request.getDescription());
        if (request.getManagerId() != null) {
            User manager = userRepository.findByIdAndIsDeletedFalse(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            department.setManager(manager);
        } else {
            department.setManager(null);
        }
        return toResponseWithMemberCount(departmentRepository.save(department));
    }

    private DepartmentResponse toResponseWithMemberCount(Department department) {
        DepartmentResponse response = departmentMapper.toResponse(department);
        response.setMemberCount((int) userRepository.countByDepartmentIdAndIsDeletedFalseAndIsActiveTrue(department.getId()));
        return response;
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        department.setIsDeleted(true);
        departmentRepository.save(department);
    }
}
