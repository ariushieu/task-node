package learning.tasknode.service;

import learning.tasknode.dto.response.ProjectProgressResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    public byte[] exportProjectProgressToExcel(List<ProjectProgressResponse> data) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Tiến độ dự án");

            // Title row
            CellStyle titleStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO TIẾN ĐỘ DỰ ÁN");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            // Header row
            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            String[] headers = {"STT", "Tên dự án", "Tổng công việc", "Đã hoàn thành", "Đang làm", "Quá hạn", "Tiến độ (task)", "Tiến độ (TB)"};
            Row headerRow = sheet.createRow(2);
            headerRow.setHeightInPoints(25);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data styles
            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle centerStyle = wb.createCellStyle();
            centerStyle.cloneStyleFrom(dataStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle percentStyle = wb.createCellStyle();
            percentStyle.cloneStyleFrom(centerStyle);
            percentStyle.setDataFormat(wb.createDataFormat().getFormat("0.0\"%\""));

            int rowIdx = 3;
            for (int i = 0; i < data.size(); i++) {
                ProjectProgressResponse row = data.get(i);
                Row r = sheet.createRow(rowIdx++);

                Cell c0 = r.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(centerStyle);

                Cell c1 = r.createCell(1);
                c1.setCellValue(row.getProjectName());
                c1.setCellStyle(dataStyle);

                Cell c2 = r.createCell(2);
                c2.setCellValue(row.getTotalTasks());
                c2.setCellStyle(centerStyle);

                Cell c3 = r.createCell(3);
                c3.setCellValue(row.getCompletedTasks());
                c3.setCellStyle(centerStyle);

                Cell c4 = r.createCell(4);
                c4.setCellValue(row.getInProgressTasks());
                c4.setCellStyle(centerStyle);

                Cell c5 = r.createCell(5);
                c5.setCellValue(row.getOverdueTasks());
                c5.setCellStyle(centerStyle);

                Cell c6 = r.createCell(6);
                c6.setCellValue(String.format("%.1f%%", row.getPercentCompleted()));
                c6.setCellStyle(centerStyle);

                Cell c7 = r.createCell(7);
                c7.setCellValue(String.format("%.1f%%", row.getAvgProgress()));
                c7.setCellStyle(centerStyle);
            }

            for (int c = 0; c < headers.length; c++) sheet.autoSizeColumn(c);
            sheet.setColumnWidth(1, Math.max(sheet.getColumnWidth(1), 6000));

            wb.write(out);
            return out.toByteArray();
        }
    }

}
