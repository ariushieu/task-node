package learning.tasknode.service;

import learning.tasknode.dto.request.TaskAssignRequest;
import learning.tasknode.dto.request.TaskCreateRequest;
import learning.tasknode.dto.request.TaskProgressUpdateRequest;
import learning.tasknode.dto.request.TaskStatusUpdateRequest;
import learning.tasknode.dto.request.TaskUpdateRequest;
import learning.tasknode.dto.request.TaskReceiveRequest;
import learning.tasknode.dto.request.TaskRejectRequest;
import learning.tasknode.dto.response.TaskResponse;
import learning.tasknode.entity.Department;
import learning.tasknode.entity.Task;
import learning.tasknode.entity.TaskAssignee;
import learning.tasknode.entity.Project;
import learning.tasknode.entity.User;
import learning.tasknode.enums.TaskStatus;
import learning.tasknode.exception.BadRequestException;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.entity.ApprovalLog;
import learning.tasknode.enums.ApprovalAction;
import learning.tasknode.mapper.TaskMapper;
import learning.tasknode.repository.ApprovalLogRepository;
import learning.tasknode.repository.DepartmentRepository;
import learning.tasknode.repository.ProjectRepository;
import learning.tasknode.repository.TaskAssigneeRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final ApprovalLogRepository approvalLogRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse createTask(TaskCreateRequest request) {
        Project project = projectRepository.findByIdAndIsDeletedFalse(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Department department = departmentRepository.findByIdAndIsDeletedFalse(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        User createdBy = getCurrentUser();

        Task task = taskMapper.toEntity(request);
        task.setProject(project);
        task.setDepartment(department);
        task.setCreatedBy(createdBy);
        if (task.getStatus() == null) task.setStatus(TaskStatus.NEW);

        return toResponseWithAssignees(taskRepository.save(task));
    }

    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAllActive(pageable).map(this::toResponseWithAssignees);
    }

    public Page<TaskResponse> getMyTasks(Pageable pageable) {
        User currentUser = getCurrentUser();
        return taskRepository.findByAssigneeUserId(currentUser.getId(), pageable)
                .map(this::toResponseWithAssignees);
    }

    public Page<TaskResponse> getMyDepartmentTasks(Pageable pageable) {
        User currentUser = getCurrentUser();
        List<Department> departments = departmentRepository.findByManagerIdAndIsDeletedFalse(currentUser.getId());
        if (departments.isEmpty()) {
            throw new BadRequestException("Bạn không phải trưởng phòng ban nào!");
        }
        List<Long> deptIds = departments.stream().map(Department::getId).toList();
        return taskRepository.findByDepartmentIdInAndIsDeletedFalse(deptIds, pageable)
                .map(this::toResponseWithAssignees);
    }

    @Transactional
    public TaskResponse assignTask(Long taskId, TaskAssignRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        User currentUser = getCurrentUser();
        Department taskDept = task.getDepartment();
        if (taskDept == null) {
            throw new BadRequestException("Task chưa được giao cho phòng ban nào!");
        }
        List<Department> managedDepts = departmentRepository.findByManagerIdAndIsDeletedFalse(currentUser.getId());
        if (managedDepts.isEmpty()) {
            throw new BadRequestException("Bạn không phải trưởng phòng ban nào!");
        }
        boolean managesTaskDept = managedDepts.stream()
                .anyMatch(d -> d.getId().equals(taskDept.getId()));
        if (!managesTaskDept) {
            throw new BadRequestException("Bạn không phải trưởng phòng ban phụ trách task này!");
        }

        task.getAssignees().clear();

        for (Long userId : request.getAssigneeIds()) {
            User assignee = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Nhân viên không tồn tại: " + userId));
            if (assignee.getDepartment() == null || !assignee.getDepartment().getId().equals(taskDept.getId())) {
                throw new BadRequestException("Nhân viên " + assignee.getFullName() + " không thuộc phòng ban phụ trách task này!");
            }
            TaskAssignee ta = TaskAssignee.builder()
                    .task(task)
                    .user(assignee)
                    .progress(0)
                    .build();
            task.getAssignees().add(ta);
        }

        if (task.getStatus() == TaskStatus.NEW) {
            task.setStatus(TaskStatus.TODO);
        }
        return toResponseWithAssignees(taskRepository.save(task));
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return toResponseWithAssignees(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskMapper.updateEntityFromDto(request, task);
        return toResponseWithAssignees(taskRepository.save(task));
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
        return toResponseWithAssignees(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateMyProgress(Long taskId, TaskProgressUpdateRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = getCurrentUser();
        TaskAssignee ta = taskAssigneeRepository.findByTaskIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Bạn không được giao task này!"));
        ta.setProgress(request.getProgress());
        taskAssigneeRepository.save(ta);
        return toResponseWithAssignees(task);
    }

    @Transactional
    public TaskResponse receiveTask(Long id, TaskReceiveRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(request.getUserId())) {
            throw new BadRequestException("Bạn chỉ có thể nhận task cho chính mình!");
        }

        boolean alreadyAssigned = task.getAssignees().stream()
                .anyMatch(ta -> ta.getUser().getId().equals(currentUser.getId()));
        if (!alreadyAssigned) {
            TaskAssignee ta = TaskAssignee.builder()
                    .task(task)
                    .user(currentUser)
                    .progress(0)
                    .build();
            task.getAssignees().add(ta);
        }

        if (task.getStatus() == TaskStatus.NEW || task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        return toResponseWithAssignees(taskRepository.save(task));
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

        for (TaskAssignee ta : task.getAssignees()) {
            ta.setProgress(100);
        }

        User approver = getCurrentUser();
        ApprovalLog log = ApprovalLog.builder()
                .task(task)
                .approver(approver)
                .action(ApprovalAction.APPROVE)
                .build();
        approvalLogRepository.save(log);

        return toResponseWithAssignees(taskRepository.save(task));
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

        return toResponseWithAssignees(taskRepository.save(task));
    }

    private TaskResponse toResponseWithAssignees(Task task) {
        TaskResponse response = taskMapper.toResponse(task);
        List<TaskResponse.AssigneeInfo> assigneeInfos = task.getAssignees().stream()
                .map(ta -> TaskResponse.AssigneeInfo.builder()
                        .id(ta.getUser().getId())
                        .fullName(ta.getUser().getFullName())
                        .avatarUrl(ta.getUser().getAvatarUrl())
                        .progress(ta.getProgress())
                        .build())
                .collect(Collectors.toList());
        response.setAssignees(assigneeInfos);

        if (assigneeInfos.isEmpty()) {
            response.setProgress(0);
        } else {
            int avg = (int) Math.round(assigneeInfos.stream()
                    .mapToInt(TaskResponse.AssigneeInfo::getProgress)
                    .average()
                    .orElse(0));
            response.setProgress(avg);
        }
        return response;
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
