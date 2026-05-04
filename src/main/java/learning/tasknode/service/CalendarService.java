package learning.tasknode.service;

import learning.tasknode.dto.response.TaskResponse;
import learning.tasknode.entity.User;
import learning.tasknode.exception.BadRequestException;
import learning.tasknode.mapper.TaskMapper;
import learning.tasknode.repository.TaskRepository;
import learning.tasknode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<TaskResponse> getCalendarTasks(String start, String end, Pageable pageable) {
        if (start == null || end == null) {
            throw new BadRequestException("Thiếu tham số ngày bắt đầu hoặc kết thúc!");
        }
        try {
            LocalDate s = LocalDate.parse(start, DF);
            LocalDate e = LocalDate.parse(end, DF);
            User currentUser = getCurrentUser();
            return taskRepository.findCalendarTasksByUser(currentUser.getId(), s, e, pageable)
                    .map(this::toResponseWithAssignees);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BadRequestException("Sai định dạng ngày! Ví dụ đúng: 2026-04-23");
        }
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private TaskResponse toResponseWithAssignees(learning.tasknode.entity.Task task) {
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
                    .average().orElse(0));
            response.setProgress(avg);
        }
        return response;
    }
}
