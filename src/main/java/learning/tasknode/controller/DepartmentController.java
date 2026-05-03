package learning.tasknode.controller;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.DepartmentCreateRequest;
import learning.tasknode.entity.Department;
import learning.tasknode.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<learning.tasknode.dto.response.DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentCreateRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<learning.tasknode.dto.response.DepartmentResponse>> getAllDepartments(org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAllDepartments(pageable));
    }
}
