package learning.tasknode.service;

import com.itextpdf.text.Font;
import learning.tasknode.dto.response.ProjectProgressResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {
    public byte[] exportProjectProgressToExcel(List<ProjectProgressResponse> data) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Project Progress");
            Row header = sheet.createRow(0);
            String[] headers = {"ID Dự án", "Tên dự án", "Tổng công việc", "Đã hoàn thành", "Đang làm", "Quá hạn", "% hoàn thành"};
            for (int i=0; i<headers.length; i++) header.createCell(i).setCellValue(headers[i]);
            int rowIdx = 1;
            for (ProjectProgressResponse row : data) {
                Row r = sheet.createRow(rowIdx++);
                r.createCell(0).setCellValue(row.getProjectId());
                r.createCell(1).setCellValue(row.getProjectName());
                r.createCell(2).setCellValue(row.getTotalTasks());
                r.createCell(3).setCellValue(row.getCompletedTasks());
                r.createCell(4).setCellValue(row.getInProgressTasks());
                r.createCell(5).setCellValue(row.getOverdueTasks());
                r.createCell(6).setCellValue(row.getPercentCompleted());
            }
            for (int c=0; c<headers.length; c++) sheet.autoSizeColumn(c);
            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] exportProjectProgressToPdf(List<ProjectProgressResponse> data) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, out);
        doc.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        String[] headers = {"ID Dự án", "Tên dự án", "Tổng công việc", "Đã hoàn thành", "Đang làm", "Quá hạn", "% hoàn thành"};
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(225,225,225));
            table.addCell(cell);
        }
        for (ProjectProgressResponse row : data) {
            table.addCell(new Phrase(String.valueOf(row.getProjectId()), cellFont));
            table.addCell(new Phrase(row.getProjectName(), cellFont));
            table.addCell(new Phrase(String.valueOf(row.getTotalTasks()), cellFont));
            table.addCell(new Phrase(String.valueOf(row.getCompletedTasks()), cellFont));
            table.addCell(new Phrase(String.valueOf(row.getInProgressTasks()), cellFont));
            table.addCell(new Phrase(String.valueOf(row.getOverdueTasks()), cellFont));
            table.addCell(new Phrase(String.format("%.1f", row.getPercentCompleted()), cellFont));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }
}
