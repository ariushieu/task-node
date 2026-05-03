package learning.tasknode.service;

import org.springframework.transaction.annotation.Transactional;
import learning.tasknode.dto.request.ProjectCreateRequest;
import learning.tasknode.dto.request.ProjectUpdateRequest;
import learning.tasknode.dto.response.ProjectResponse;
import learning.tasknode.dto.response.UserResponse;
import learning.tasknode.entity.*;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.mapper.ProjectMapper;
import learning.tasknode.mapper.UserMapper;
import learning.tasknode.repository.ProjectRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request) {
        User owner = userRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectMapper.toEntity(request);
        project.setOwner(owner);
        project = projectRepository.save(project);
        return toResponseWithMemberCount(project);
    }

    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAllActive(pageable).map(this::toResponseWithMemberCount);
    }

    public ProjectResponse getProject(Long id) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return toResponseWithMemberCount(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        projectMapper.updateEntityFromDto(request, project);
        return toResponseWithMemberCount(projectRepository.save(project));
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setIsDeleted(true);
        projectRepository.save(project);
    }

    public List<UserResponse> getProjectAssignees(Long projectId) {
        projectRepository.findByIdAndIsDeletedFalse(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return taskRepository.findDistinctAssigneesByProjectId(projectId)
                .stream().map(userMapper::toResponse).toList();
    }

    private ProjectResponse toResponseWithMemberCount(Project project) {
        ProjectResponse response = projectMapper.toResponse(project);
        response.setMemberCount(taskRepository.countDistinctAssigneesByProjectId(project.getId()));
        return response;
    }
}
