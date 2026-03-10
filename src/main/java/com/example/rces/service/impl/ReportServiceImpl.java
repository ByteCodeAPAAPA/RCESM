package com.example.rces.service.impl;

import com.example.rces.dto.report.InspectionReportModel;
import com.example.rces.dto.report.SpeFgisReportModel;
import com.example.rces.dto.report.SpeReportModel;
import com.example.rces.dto.report.SpeScheduleReportModel;
import com.example.rces.models.*;
import com.example.rces.models.enums.Format;
import com.example.rces.models.enums.Status;
import com.example.rces.service.ReportService;
import com.example.rces.utils.JasperReportExporter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.example.rces.service.impl.CustomUserDetailsServiceImpl.currentUser;
import static com.example.rces.utils.WordExporter.generateManyWordFile;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class ReportServiceImpl implements ReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final JasperReportExporter jasperReportExporter;

    @Autowired
    public ReportServiceImpl(JasperReportExporter jasperReportExporter) {
        this.jasperReportExporter = jasperReportExporter;
    }

    //TODO переделать под JasperReports
    @Override
    public List<SGI> getSgiList(List<UUID> ids, String department) {
        if (department != null) {
            return entityManager.createQuery(
                            "SELECT e FROM SGI e " +
                                    "WHERE e.department = :department " +
                                    "ORDER BY e.requestNumber ASC", SGI.class)
                    .setParameter("department", SGI.Department.fromName(department))
                    .getResultList();
        } else {
            return entityManager.createQuery(
                            "SELECT e FROM SGI e " +
                                    "WHERE e.id IN (:ids)")
                    .setParameter("ids", ids)
                    .getResultList();
        }
    }

    //TODO переделать под JasperReports
    @Override
    public ByteArrayResource getExcelFile(List<SGI> sgiList) {
        try {
            return generateManyWordFile(sgiList, List.of(
                    "№ п/п", "№ цеха", "Мероприятие", "Сопутствующие действия",
                    "Ответственный отдел", "Ответственное лицо", "Желаемый срок",
                    "Примечание", "Планируемый срок", "Комментарий", "Статус"));
        } catch (Exception e) {
            throw new ApplicationContextException("Ошибка при создании отчета СГИ", e);
        }
    }

    //TODO переделать под JasperReports
    @Override
    public byte[] reportBid() throws IOException {
        Employee user = currentUser().orElseThrow();
        List<Requests> rejectedBid = entityManager.createQuery(
                        "SELECT e FROM Requests e " +
                                "WHERE e.subDivision = :subDivision AND e.status = :status", Requests.class)
                .setParameter("subDivision", user.getSubDivision())
                .setParameter("status", Status.Rejected)
                .getResultList();

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Rejected Bids");

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(user.getSubDivision().getName() + " отчет о забракованной продукции");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);

            Row headerRow = sheet.createRow(1);
            headerRow.createCell(0).setCellValue("№");
            headerRow.createCell(1).setCellValue("Обозначение/Наименование");
            headerRow.createCell(2).setCellValue("ЗК");
            headerRow.createCell(3).setCellValue("Дата");

            int rowNum = 2;
            for (Requests reject : rejectedBid) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(reject.getRequestNumber());
                row.createCell(1).setCellValue(reject.getTitle() != null ? reject.getTitle() : "");
                row.createCell(2).setCellValue(reject.getCustomerOrder().getName());
                row.createCell(3).setCellValue(reject.getUpdatedDate() != null
                        ? LocalDateTime.from(reject.getUpdatedDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        : "");
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Override
    public byte[] createSpeReport(Format format, List<Integer> numberList) {
        List<SPE> speList = entityManager.createQuery(
                        "SELECT e FROM SPE e WHERE e.id IN (:ids) ORDER BY e.number ASC")
                .setParameter("ids", numberList).getResultList();
        SpeReportModel model = new SpeReportModel(speList);
        return jasperReportExporter.generateJrxmlReport("Spe", null, List.of(model), format);
    }

    @Override
    public byte[] createSpeFgisReport(JsonNode data) {
        SpeFgisReportModel model = new SpeFgisReportModel(data);
        return jasperReportExporter.generateJrxmlReport("SpeFgis", null, List.of(model), Format.PDF);
    }

    @Override
    public byte[] createSpeSchedule(Format format, List<Integer> numberList) {
        List<SPE> speList = entityManager.createQuery(
                        "SELECT e FROM SPE e WHERE e.id IN (:ids) AND e.organization IS NOT NULL ORDER BY e.number ASC")
                .setParameter("ids", numberList).getResultList();
        SpeScheduleReportModel model = new SpeScheduleReportModel(speList);
        return jasperReportExporter.generateJrxmlReport("SpeSchedule", null, List.of(model), format);
    }

    @Override
    public byte[] createInspectionReport(Format format, Integer id) {
        InspectionReportModel model;
        if (id != null) {
            Inspection inspection = entityManager.createQuery(
                            "SELECT e FROM Inspection e " +
                                    "LEFT JOIN FETCH  e.createdBy cb " +
                                    "LEFT JOIN FETCH  e.primaryInspection p " +
                                    "LEFT JOIN FETCH  e.subDivision s " +
                                    "WHERE e.id = :id", Inspection.class)
                    .setParameter("id", id).getSingleResult();
            entityManager.createQuery(
                            "SELECT e from InspectionViolation e " +
                                    "LEFT JOIN FETCH e.subDivision s " +
                                    "LEFT JOIN FETCH e.images i " +
                                    "LEFT JOIN FETCH e.createdBy cb " +
                                    "LEFT JOIN FETCH cb.subDivision cbs " +
                                    "WHERE e.id in (:id)")
                    .setParameter("id", inspection.getViolation().stream().map(InspectionViolation::getId).toList());
            model = new InspectionReportModel(inspection);
        } else {
            List<InspectionViolation> violation =
                    entityManager.createNamedQuery("InspectionViolation.findAllNotFixed", InspectionViolation.class)
                            .getResultList();
            model = new InspectionReportModel(violation);
        }
        return jasperReportExporter.generateJrxmlReport(
                "InspectionWorkShop", null, List.of(model), format
        );
    }

}
