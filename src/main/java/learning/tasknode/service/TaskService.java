package learning.tasknode.service;

import jakarta.transaction.Transactional;
import learning.tasknode.dto.request.TaskCreateRequest;
import learning.tasknode.dto.request.TaskStatusUpdateRequest;
import learning.tasknode.dto.request.TaskUpdateRequest;
import learning.tasknode.dto.request.TaskReceiveRequest;
import learning.tasknode.dto.request.TaskRejectRequest;
import learning.tasknode.dto.response.TaskResponse;
import learning.tasknode.entity.Task;
import learning.tasknode.entity.Project;
import learning.tasknode.entity.User;
import learning.tasknode.enums.TaskStatus;
import learning.tasknode.exception.BadRequestException;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.entity.ApprovalLog;
import learning.tasknode.enums.ApprovalAction;
import learning.tasknode.mapper.TaskMapper;
import learning.tasknode.repository.ApprovalLogRepository;
import learning.tasknode.repository.ProjectRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApprovalLogRepository approvalLogRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User createdBy = getCurrentUser();

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        }

        Task task = taskMapper.toEntity(request);
        task.setProject(project);
        task.setCreatedBy(createdBy);
        if (assignee != null) task.setAssignee(assignee);
        if (task.getStatus() == null) task.setStatus(TaskStatus.NEW);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAllActive(pageable).map(taskMapper::toResponse);
    }

    public Page<TaskResponse> getMyTasks(Pageable pageable) {
        User currentUser = getCurrentUser();
        return taskRepository.findByAssigneeIdAndIsDeletedFalse(currentUser.getId(), pageable)
                .map(taskMapper::toResponse);
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }
        taskMapper.updateEntityFromDto(request, task);
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(request.getStatus());
        if (request.getStatus() == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse receiveTask(Long id, TaskReceiveRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(request.getUserId())) {
            throw new BadRequestException("Bạn chỉ có thể nhận task cho chính mình!");
        }
        task.setAssignee(currentUser);
        if (task.getStatus() == TaskStatus.NEW || task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse approveTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc phù hợp!"));
        if (task.getStatus() == TaskStatus.DONE)
            throw new BadRequestException("Công việc đã ở trạng thái hoàn thành!");
        if (task.getStatus() == TaskStatus.REJECTED)
            throw new BadRequestException("Công việc đã bị từ chối! Không thể duyệt.");
        if (task.getStatus() != TaskStatus.WAITING_APPROVAL)
            throw new BadRequestException("Công việc phải ở trạng thái chờ duyệt mới có thể phê duyệt!");
        task.setStatus(TaskStatus.APPROVED);
        task.setCompletedAt(LocalDateTime.now());

        User approver = getCurrentUser();
        ApprovalLog log = ApprovalLog.builder()
                .task(task)
                .approver(approver)
                .action(ApprovalAction.APPROVE)
                .build();
        approvalLogRepository.save(log);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse rejectTask(Long id, TaskRejectRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc phù hợp!"));
        if (task.getStatus() == TaskStatus.DONE)
            throw new BadRequestException("Công việc đã hoàn thành! Không thể từ chối.");
        if (task.getStatus() == TaskStatus.REJECTED)
            throw new BadRequestException("Công việc đã bị từ chối trước đó!");
        if (task.getStatus() != TaskStatus.WAITING_APPROVAL)
            throw new BadRequestException("Công việc phải ở trạng thái chờ duyệt mới có thể từ chối!");
        task.setStatus(TaskStatus.REJECTED);

        User approver = getCurrentUser();
        ApprovalLog log = ApprovalLog.builder()
                .task(task)
                .approver(approver)
                .action(ApprovalAction.REJECT)
                .reason(request.getReason())
                .build();
        approvalLogRepository.save(log);

        return taskMapper.toResponse(taskRepository.save(task));
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String s) {
            username = s;
        } else {
            throw new BadRequestException("Không xác định được người dùng hiện tại!");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }
}
