package learning.tasknode.controller;
import learning.tasknode.dto.response.EmployeePerformanceResponse;
import learning.tasknode.dto.response.ProjectProgressResponse;
import learning.tasknode.service.ReportService;
import learning.tasknode.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ExportService exportService;

    @GetMapping("/employee-performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<org.springframework.data.domain.Page<EmployeePerformanceResponse>> getEmployeePerformance(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end, org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(reportService.getEmployeePerformance(start, end, pageable));
    }

    @GetMapping("/project-progress")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<org.springframework.data.domain.Page<ProjectProgressResponse>> getProjectProgress(org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(reportService.getProjectProgress(pageable));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<byte[]> exportProjectProgress(org.springframework.data.domain.Pageable pageable) throws Exception {
        org.springframework.data.domain.Page<ProjectProgressResponse> page = reportService.getProjectProgress(pageable);
        byte[] excel = exportService.exportProjectProgressToExcel(page.getContent());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=project-progress.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excel);
    }
}