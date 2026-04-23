package learning.tasknode.controller;
import learning.tasknode.dto.response.EmployeePerformanceResponse;
import learning.tasknode.dto.response.ProjectProgressResponse;
import learning.tasknode.service.ReportService;
import learning.tasknode.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final ExportService exportService;

    @GetMapping("/employee-performance")
    public ResponseEntity<org.springframework.data.domain.Page<EmployeePerformanceResponse>> getEmployeePerformance(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end, org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(reportService.getEmployeePerformance(start, end, pageable));
    }

    @GetMapping("/project-progress")
    public ResponseEntity<org.springframework.data.domain.Page<ProjectProgressResponse>> getProjectProgress(org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(reportService.getProjectProgress(pageable));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProjectProgress(
            @RequestParam(required = false, defaultValue = "excel") String type) throws Exception {
        List<ProjectProgressResponse> data = reportService.getProjectProgress();
        if ("pdf".equalsIgnoreCase(type)) {
            byte[] pdf = exportService.exportProjectProgressToPdf(data);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=project-progress.pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);
        } else {
            byte[] excel = exportService.exportProjectProgressToExcel(data);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=project-progress.xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(excel);
        }
    }
}