package learning.tasknode.service;

import jakarta.transaction.Transactional;
import learning.tasknode.dto.request.CommentRequest;
import learning.tasknode.dto.response.CommentResponse;
import learning.tasknode.entity.Comment;
import learning.tasknode.entity.ProjectMember;
import learning.tasknode.entity.Task;
import learning.tasknode.entity.User;
import learning.tasknode.exception.BadRequestException;
import learning.tasknode.exception.ResourceNotFoundException;
import learning.tasknode.mapper.CommentMapper;
import learning.tasknode.repository.CommentRepository;
import learning.tasknode.repository.ProjectMemberRepository;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final CommentMapper commentMapper;
    private final NotificationWebSocketService notificationWebSocketService;

    public List<CommentResponse> getCommentsOfTask(Long taskId, Long currentUserId) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc phù hợp!"));
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));
        Long projectId = task.getProject().getId();
        boolean isProjectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
            .filter(pm -> !Boolean.TRUE.equals(pm.getIsDeleted())).isPresent();
        if (!isProjectMember) {
            throw new BadRequestException("Bạn không có quyền xem bình luận do không thuộc dự án này!");
        }
        List<Comment> comments = commentRepository.findByTaskIdAndIsDeletedFalseOrderByCreatedAtAsc(taskId);
        return comments.stream().map(commentMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse addComment(Long taskId, CommentRequest request) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc phù hợp!"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));
        // Kiểm tra: user phải thuộc cùng dự án
        Long projectId = task.getProject().getId();
        boolean isProjectMember = projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .filter(pm -> !Boolean.TRUE.equals(pm.getIsDeleted())).isPresent();
        if (!isProjectMember) {
            throw new BadRequestException("Bạn không thuộc dự án này, không thể tham gia bình luận!");
        }
        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(request.getContent())
                .isDeleted(false)
                .build();
        comment = commentRepository.save(comment);
        // Push notify realtime cho mọi thành viên project (trừ người bình luận)
        List<ProjectMember> members = projectMemberRepository.findByProjectIdAndIsDeletedFalse(projectId);
        for (ProjectMember member : members) {
            if (!member.getUser().getId().equals(user.getId())) {
                notificationWebSocketService.sendNotification(
                    member.getUser().getId(),
                    user.getFullName() + " vừa bình luận vào công việc: " + task.getTitle()
                );
            }
        }
        return commentMapper.toResponse(comment);
    }
}
