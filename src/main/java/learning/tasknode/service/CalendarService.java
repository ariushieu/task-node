package learning.tasknode.service;

import learning.tasknode.dto.response.TaskResponse;
import learning.tasknode.exception.BadRequestException;
import learning.tasknode.mapper.TaskMapper;
import learning.tasknode.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Page<TaskResponse> getCalendarTasks(String start, String end, Pageable pageable) {
        if (start == null || end == null) {
            throw new BadRequestException("Thiếu tham số ngày bắt đầu hoặc kết thúc!");
        }
        try {
            LocalDateTime s = LocalDate.parse(start, DF).atStartOfDay();
            LocalDateTime e = LocalDate.parse(end, DF).atTime(23, 59, 59);
            return taskRepository.findByCalendarRange(s, e, pageable).map(taskMapper::toResponse);
        } catch (Exception ex) {
            throw new BadRequestException("Sai định dạng ngày! Ví dụ đúng: 2026-04-23");
        }
    }
}
