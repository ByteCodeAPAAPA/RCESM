package com.example.rces.utils;

import jakarta.validation.constraints.NotNull;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    //Пока только под ЗК
    public static ByteArrayResource exportToExcelTree(@NotNull List<TreeNode> rootNodes, @NotNull Map<String, Integer> columnMap) throws Exception {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Дерево");

            List<String> headers = new ArrayList<>(columnMap.keySet());
            List<Integer> headersWidth = new ArrayList<>(columnMap.values());


            for (int i = 0; i < headersWidth.size(); i++) sheet.setColumnWidth(i, headersWidth.get(i));

            // Стили
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle defaultStyle = createDefaultStyle(wb);
            Map<Integer, CellStyle> indentStyles = new HashMap<>();

            // Заголовки
            int rowIndex = 0;
            Row headerRow = sheet.createRow(rowIndex++);
            for (int c = 0; c < headers.size(); c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(headers.get(c));
                cell.setCellStyle(headerStyle);
            }
            sheet.createFreezePane(0, 1);

            RowRangeCounter counter = new RowRangeCounter(rowIndex);
            for (TreeNode root : rootNodes) {
                writeNode(wb, sheet, root, 0, counter, indentStyles, defaultStyle);
            }

            sheet.setRowSumsBelow(false);
            sheet.setRowSumsRight(false);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }

    private static class RowRangeCounter {
        int currentRow;

        RowRangeCounter(int startRow) {
            this.currentRow = startRow;
        }
    }

    private static int writeNode(Workbook wb, Sheet sheet, TreeNode node, int depth, RowRangeCounter counter, Map<Integer,
            CellStyle> indentStyles, CellStyle defaultStyle) {

        int myRowIdx = counter.currentRow;
        Row row = sheet.createRow(myRowIdx);

        // Стиль с отступом
        CellStyle indentStyle = indentStyles.get(depth);
        if (indentStyle == null) {
            CellStyle s = wb.createCellStyle();
            s.cloneStyleFrom(defaultStyle);
            short indent = (short) Math.min(depth, 8);
            s.setIndention(indent);
            indentStyles.put(depth, s);
            indentStyle = s;
        }

        // Заполнение колонок
        row.createCell(0).setCellValue(node.primaryDemand);
        row.getCell(0).setCellStyle(indentStyle);

        row.createCell(1).setCellValue(node.item);
        row.getCell(1).setCellStyle(defaultStyle);

        row.createCell(2).setCellValue(node.mlmNode);
        row.getCell(2).setCellStyle(defaultStyle);

        row.createCell(3).setCellValue(node.description);
        row.getCell(3).setCellStyle(defaultStyle);

        row.createCell(4).setCellValue(node.qty);
        row.getCell(4).setCellStyle(defaultStyle);

        row.createCell(5).setCellValue(node.qtyFinished);
        row.getCell(5).setCellStyle(defaultStyle);

        row.createCell(6).setCellValue(node.resourceTime);
        row.getCell(6).setCellStyle(defaultStyle);

        row.createCell(7).setCellValue(node.dateStart);
        row.getCell(7).setCellStyle(defaultStyle);

        row.createCell(8).setCellValue(node.dateEnd);
        row.getCell(8).setCellStyle(defaultStyle);

        row.createCell(9).setCellValue(node.dateCalcStart);
        row.getCell(9).setCellStyle(defaultStyle);

        row.createCell(10).setCellValue(node.dateCalcEnd);
        row.getCell(10).setCellStyle(defaultStyle);

        counter.currentRow++;

        int lastRowUsed = myRowIdx;

        if (node.children != null && !node.children.isEmpty()) {
            int firstChildRow = counter.currentRow;
            for (TreeNode child : node.children) {
                lastRowUsed = writeNode(wb, sheet, child, depth + 1, counter, indentStyles, defaultStyle);
            }
            int lastChildRow = counter.currentRow - 1;
            if (lastChildRow >= firstChildRow) {
                sheet.groupRow(firstChildRow, lastChildRow);
                sheet.setRowGroupCollapsed(firstChildRow, true);
            }
            lastRowUsed = Math.max(lastRowUsed, lastChildRow);
        }

        return lastRowUsed;
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);
        return style;
    }

    private static CellStyle createDefaultStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}