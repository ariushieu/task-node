package learning.tasknode.controller;

import jakarta.validation.Valid;
import learning.tasknode.dto.request.ProjectCreateRequest;
import learning.tasknode.dto.request.ProjectUpdateRequest;
import learning.tasknode.dto.response.ProjectResponse;
import learning.tasknode.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.getAllProjects(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @Valid @RequestBody ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<learning.tasknode.dto.response.ProjectMemberResponse> addMemberToProject(
            @PathVariable Long projectId,
            @Valid @RequestBody learning.tasknode.dto.request.ProjectMemberRequest request) {
        return ResponseEntity.ok(projectService.addMember(projectId, request));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromProject(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<org.springframework.data.domain.Page<learning.tasknode.dto.response.ProjectMemberResponse>> listProjectMembers(@PathVariable Long projectId, org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(projectService.listMembers(projectId, pageable));
    }
}
