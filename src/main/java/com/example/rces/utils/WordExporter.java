package com.example.rces.utils;

import com.example.rces.models.SGI;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.List;

import static com.example.rces.utils.DateUtil.formatedDate;

public class WordExporter {

    public static ByteArrayResource generateManyWordFile(List<SGI> sgiList, List<String> headers) throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {

            CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
            CTPageSz pageSz = sectPr.addNewPgSz();
            pageSz.setOrient(STPageOrientation.LANDSCAPE);
            pageSz.setW(BigInteger.valueOf(15840));
            pageSz.setH(BigInteger.valueOf(12240));

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Список мероприятий (" + sgiList.size() + ")");
            titleRun.setBold(true);
            titleRun.setFontSize(20);

            XWPFTable table = document.createTable(1, 11);
            table.setWidth("100%");

            CTTblPr tblPr = table.getCTTbl().getTblPr();
            CTTblBorders borders = tblPr.addNewTblBorders();

            borders.addNewBottom().setVal(STBorder.SINGLE);
            borders.addNewLeft().setVal(STBorder.SINGLE);
            borders.addNewRight().setVal(STBorder.SINGLE);
            borders.addNewTop().setVal(STBorder.SINGLE);
            borders.addNewInsideH().setVal(STBorder.SINGLE);
            borders.addNewInsideV().setVal(STBorder.SINGLE);
            borders.getBottom().setSz(BigInteger.valueOf(8));
            borders.getLeft().setSz(BigInteger.valueOf(8));
            borders.getRight().setSz(BigInteger.valueOf(8));
            borders.getTop().setSz(BigInteger.valueOf(8));
            borders.getInsideH().setSz(BigInteger.valueOf(8));
            borders.getInsideV().setSz(BigInteger.valueOf(8));

            XWPFTableRow headerRow = table.getRow(0);

            for (int i = 0; i < headers.size(); i++) {
                XWPFTableCell cell = headerRow.getCell(i);
                setCellText(cell, headers.get(i), true);
                cell.setColor("007bff"); // Синий фон

                setCellBorders(cell);
            }

            for (SGI sgi : sgiList) {
                XWPFTableRow row = table.createRow();

                setCellText(row.getCell(0), String.valueOf(sgi.getRequestNumber()), false);
                setCellText(row.getCell(1), sgi.getWorkShop() != null ? sgi.getWorkShop() : "", false);
                setCellText(row.getCell(2), sgi.getEvent() != null ? sgi.getEvent() : "", false);
                setCellText(row.getCell(3), sgi.getActions() != null ? sgi.getActions() : "", false);
                setCellText(row.getCell(4), sgi.getDepartment() != null ? sgi.getDepartment().getName() : "", false);
                setCellText(row.getCell(5), sgi.getEmployee() != null ? sgi.getEmployee().getName() : "", false);
                setCellText(row.getCell(6), sgi.getDesiredDate() != null ? formatedDate(sgi.getDesiredDate()) : "", false);
                setCellText(row.getCell(7), sgi.getNote() != null ? sgi.getNote() : "", false);
                setCellText(row.getCell(8), sgi.getPlanDate() != null ? formatedDate(sgi.getPlanDate()) : "", false);
                setCellText(row.getCell(9), sgi.getComment() != null ? sgi.getComment() : "", false);
                setCellText(row.getCell(10), sgi.getAgreed() ? "Выполнено" : "Не выполнено", false);

                String colorHex = getColorHex(sgi.getColor().name());
                for (XWPFTableCell cell : row.getTableCells()) {
                    cell.setColor(colorHex);
                    setCellBorders(cell);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }

    private static void setCellBorders(XWPFTableCell cell) {
        CTTcPr tcPr = cell.getCTTc().addNewTcPr();
        CTTcBorders borders = tcPr.addNewTcBorders();

        borders.addNewBottom().setVal(STBorder.SINGLE);
        borders.addNewLeft().setVal(STBorder.SINGLE);
        borders.addNewRight().setVal(STBorder.SINGLE);
        borders.addNewTop().setVal(STBorder.SINGLE);

        // Устанавливаем толщину границ (1pt)
        borders.getBottom().setSz(BigInteger.valueOf(8));
        borders.getLeft().setSz(BigInteger.valueOf(8));
        borders.getRight().setSz(BigInteger.valueOf(8));
        borders.getTop().setSz(BigInteger.valueOf(8));
    }

    private static String getColorHex(String color) {
        if (color == null) return "FFFFFF";
        return switch (color) {
            case "RED" -> "FFC7CE";
            case "GREEN" -> "C6EFCE";
            case "YELLOW" -> "FFEB9C";
            case "GREY" -> "D9D9D9";
            default -> "FFFFFF";
        };
    }

    private static void setCellText(XWPFTableCell cell, String text, boolean isHeader) {
        if (cell == null) return;
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text != null ? text : "");
        if (isHeader) {
            run.setColor("FFFFFF");
            run.setBold(true);
        }
    }
}
