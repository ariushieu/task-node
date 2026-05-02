package learning.tasknode.service;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.DepartmentCreateRequest;
import learning.tasknode.dto.response.DepartmentResponse;
import learning.tasknode.entity.Department;
import learning.tasknode.mapper.DepartmentMapper;
import learning.tasknode.repository.DepartmentRepository;
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

    @Transactional
    public DepartmentResponse createDepartment(@Valid DepartmentCreateRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Department name already exists");
        }
        Department department = departmentMapper.toEntity(request);
        Department saved = departmentRepository.save(department);
        return departmentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllActive(pageable)
            .map(departmentMapper::toResponse);
    }
}
