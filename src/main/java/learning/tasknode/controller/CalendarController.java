package learning.tasknode.controller;

import learning.tasknode.dto.response.DailyProgressResponse;
import learning.tasknode.dto.response.TaskResponse;
import learning.tasknode.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponse>> getTasksForCalendar(
            @RequestParam String start,
            @RequestParam String end,
            Pageable pageable) {
        return ResponseEntity.ok(calendarService.getCalendarTasks(start, end, pageable));
    }

    @GetMapping("/progress")
    public ResponseEntity<List<DailyProgressResponse>> getDailyProgress(
            @RequestParam Long taskId,
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(calendarService.getDailyProgress(taskId, start, end));
    }
}
