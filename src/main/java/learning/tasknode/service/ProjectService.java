package learning.tasknode.service;

import org.springframework.transaction.annotation.Transactional;
import learning.tasknode.dto.request.ProjectCreateRequest;
import learning.tasknode.dto.request.ProjectMemberRequest;
import learning.tasknode.dto.request.ProjectUpdateRequest;
import learning.tasknode.dto.response.ProjectMemberResponse;
import learning.tasknode.dto.response.ProjectResponse;
import learning.tasknode.entity.*;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.mapper.ProjectMapper;
import learning.tasknode.mapper.ProjectMemberMapper;
import learning.tasknode.repository.ProjectMemberRepository;
import learning.tasknode.repository.ProjectRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMemberMapper projectMemberMapper;

    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectMapper.toEntity(request);
        project.setOwner(owner);
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAllActive(pageable).map(projectMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return projectMapper.toResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectMapper.updateEntityFromDto(request, project);
        return projectMapper.toResponse(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setIsDeleted(true);
        projectRepository.save(project);
    }

    /**
     * Add a member to a project.
     * If the member was previously removed (soft-deleted), reactivate instead of creating duplicate.
     */
    @Transactional
    public ProjectMemberResponse addMember(Long projectId, ProjectMemberRequest request) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var existing = projectMemberRepository.findByProjectIdAndUserIdIncludeDeleted(projectId, request.getUserId());
        if (existing.isPresent()) {
            ProjectMember member = existing.get();
            if (Boolean.TRUE.equals(member.getIsDeleted())) {
                // Reactivate soft-deleted member
                member.setIsDeleted(false);
                member.setDeletedAt(null);
                member.setRoleInProject(request.getRoleInProject());
                return projectMemberMapper.toResponse(projectMemberRepository.save(member));
            }
            throw new IllegalArgumentException("User already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(user)
                .roleInProject(request.getRoleInProject())
                .isDeleted(false)
                .build();
        ProjectMember saved = projectMemberRepository.save(member);
        return projectMemberMapper.toResponse(saved);
    }

    /**
     * Remove a member from project (soft delete)
     */
    @Transactional
    public void removeMember(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found in project"));
        member.setIsDeleted(true);
        projectMemberRepository.save(member);
    }

    /**
     * List project members
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ProjectMemberResponse> listMembers(Long projectId, org.springframework.data.domain.Pageable pageable) {
        return projectMemberRepository.findByProjectIdAndIsDeletedFalse(projectId, pageable)
                .map(projectMemberMapper::toResponse);
    }
}
