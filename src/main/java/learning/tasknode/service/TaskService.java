package learning.tasknode.service;

import learning.tasknode.dto.request.TaskAssignRequest;
import learning.tasknode.dto.request.TaskCreateRequest;
import learning.tasknode.dto.request.TaskProgressUpdateRequest;
import learning.tasknode.dto.request.TaskStatusUpdateRequest;
import learning.tasknode.dto.request.TaskUpdateRequest;
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
import learning.tasknode.repository.TaskProgressLogRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final TaskProgressLogRepository taskProgressLogRepository;
    private final ApprovalLogRepository approvalLogRepository;
    private final TaskMapper taskMapper;

    private static final java.util.Map<TaskStatus, java.util.Set<TaskStatus>> VALID_TRANSITIONS = java.util.Map.of(
        TaskStatus.NEW, java.util.Set.of(TaskStatus.TODO),
        TaskStatus.TODO, java.util.Set.of(TaskStatus.IN_PROGRESS),
        TaskStatus.IN_PROGRESS, java.util.Set.of(TaskStatus.IN_REVIEW, TaskStatus.WAITING_APPROVAL),
        TaskStatus.IN_REVIEW, java.util.Set.of(TaskStatus.IN_PROGRESS, TaskStatus.WAITING_APPROVAL),
        TaskStatus.WAITING_APPROVAL, java.util.Set.of(TaskStatus.DONE, TaskStatus.REJECTED),
        TaskStatus.REJECTED, java.util.Set.of(TaskStatus.IN_PROGRESS),
        TaskStatus.DONE, java.util.Set.of()
    );

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
        task.setStatus(TaskStatus.NEW);

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

        // Remove assignees not in the new list
        java.util.Set<Long> newIds = new java.util.HashSet<>(request.getAssigneeIds());
        task.getAssignees().removeIf(ta -> !newIds.contains(ta.getUser().getId()));

        // Find existing assignee user IDs
        java.util.Set<Long> existingIds = task.getAssignees().stream()
                .map(ta -> ta.getUser().getId())
                .collect(Collectors.toSet());

        // Add new assignees
        for (Long userId : request.getAssigneeIds()) {
            if (existingIds.contains(userId)) continue;
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
        if (task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.WAITING_APPROVAL) {
            throw new BadRequestException("Không thể chỉnh sửa task đã hoàn thành hoặc đang chờ duyệt!");
        }
        taskMapper.updateEntityFromDto(request, task);
        return toResponseWithAssignees(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getStatus() == TaskStatus.IN_PROGRESS || task.getStatus() == TaskStatus.WAITING_APPROVAL) {
            throw new BadRequestException("Không thể xóa task đang thực hiện hoặc đang chờ duyệt!");
        }
        task.setIsDeleted(true);
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long id, TaskStatusUpdateRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        TaskStatus current = task.getStatus();
        TaskStatus target = request.getStatus();

        // WAITING_APPROVAL and DONE transitions are handled by dedicated endpoints
        if (target == TaskStatus.DONE) {
            throw new BadRequestException("Sử dụng chức năng phê duyệt để hoàn thành task!");
        }
        if (target == TaskStatus.WAITING_APPROVAL) {
            // Only assignees can submit for approval
            User currentUser = getCurrentUser();
            boolean isAssignee = task.getAssignees().stream()
                    .anyMatch(ta -> ta.getUser().getId().equals(currentUser.getId()));
            if (!isAssignee) {
                throw new BadRequestException("Chỉ người được giao mới có thể nộp duyệt!");
            }
        }

        java.util.Set<TaskStatus> allowed = VALID_TRANSITIONS.getOrDefault(current, java.util.Set.of());
        if (!allowed.contains(target)) {
            throw new BadRequestException("Không thể chuyển trạng thái từ " + current + " sang " + target + "!");
        }

        task.setStatus(target);
        return toResponseWithAssignees(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateMyProgress(Long taskId, TaskProgressUpdateRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new BadRequestException("Chỉ có thể cập nhật tiến độ khi task đang thực hiện!");
        }
        User currentUser = getCurrentUser();
        TaskAssignee ta = taskAssigneeRepository.findByTaskIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Bạn không được giao task này!"));
        ta.setProgress(request.getProgress());
        taskAssigneeRepository.save(ta);

        LocalDate today = LocalDate.now();
        learning.tasknode.entity.TaskProgressLog log = taskProgressLogRepository
                .findByTaskIdAndUserIdAndLogDate(taskId, currentUser.getId(), today)
                .orElse(learning.tasknode.entity.TaskProgressLog.builder()
                        .task(task)
                        .user(currentUser)
                        .logDate(today)
                        .build());
        log.setProgress(request.getProgress());
        taskProgressLogRepository.save(log);

        return toResponseWithAssignees(task);
    }

    @Transactional
    public TaskResponse receiveTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = getCurrentUser();

        // Only allow receiving tasks in TODO or IN_PROGRESS status
        if (task.getStatus() != TaskStatus.TODO && task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new BadRequestException("Chỉ có thể nhận task ở trạng thái Chờ làm hoặc Đang làm!");
        }

        // Check department
        Department taskDept = task.getDepartment();
        if (taskDept != null && currentUser.getDepartment() != null
                && !currentUser.getDepartment().getId().equals(taskDept.getId())) {
            throw new BadRequestException("Bạn không thuộc phòng ban phụ trách task này!");
        }

        boolean alreadyAssigned = task.getAssignees().stream()
                .anyMatch(ta -> ta.getUser().getId().equals(currentUser.getId()));
        if (alreadyAssigned) {
            throw new BadRequestException("Bạn đã được giao task này rồi!");
        }

        TaskAssignee ta = TaskAssignee.builder()
                .task(task)
                .user(currentUser)
                .progress(0)
                .build();
        task.getAssignees().add(ta);

        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
        return toResponseWithAssignees(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse approveTask(Long id) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc!"));

        if (task.getStatus() != TaskStatus.WAITING_APPROVAL) {
            throw new BadRequestException("Công việc phải ở trạng thái chờ duyệt mới có thể phê duyệt!");
        }

        validateApprover(task);

        task.setStatus(TaskStatus.DONE);
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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc!"));

        if (task.getStatus() != TaskStatus.WAITING_APPROVAL) {
            throw new BadRequestException("Công việc phải ở trạng thái chờ duyệt mới có thể từ chối!");
        }

        validateApprover(task);

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

    private void validateApprover(Task task) {
        User currentUser = getCurrentUser();
        Department taskDept = task.getDepartment();
        if (taskDept != null) {
            List<Department> managedDepts = departmentRepository.findByManagerIdAndIsDeletedFalse(currentUser.getId());
            boolean managesTaskDept = managedDepts.stream()
                    .anyMatch(d -> d.getId().equals(taskDept.getId()));
            if (!managesTaskDept && !currentUser.getRole().name().equals("ADMIN")) {
                throw new BadRequestException("Bạn không phải trưởng phòng ban phụ trách task này!");
            }
        }
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
