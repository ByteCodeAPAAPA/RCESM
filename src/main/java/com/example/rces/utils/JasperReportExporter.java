package com.example.rces.utils;

import com.example.rces.models.enums.Format;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JasperReportExporter {

    public <T> byte[] generateJrxmlReport(String reportName,
                                          Map<String, Object> parameters,
                                          List<T> data,
                                          Format type) {
        try (InputStream reportStream = getClass().getResourceAsStream(String.format("/reports/%s.jrxml", reportName))) {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JRDataSource dataSource = new JRBeanCollectionDataSource(data);
            if (parameters == null) {
                parameters = new HashMap<>();
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            return exportReport(jasperPrint, type);
        } catch (Exception e) {
            throw new ApplicationContextException(String.format("Ошибка при генерации отчета %s", reportName), e);
        }
    }

    private byte[] exportReport(JasperPrint jasperPrint, Format format) throws JRException {
        return switch (format) {
            case XLS -> exportToXls(jasperPrint);
            case XLSX -> exportToXlsx(jasperPrint);
            case PDF -> exportToPdf(jasperPrint);
            default -> throw new IllegalArgumentException("Неподдерживаемый формат отчета: " + format);
        };
    }

    private byte[] exportToXls(JasperPrint jasperPrint) throws JRException {
        SimpleXlsReportConfiguration config = new SimpleXlsReportConfiguration();
        config.setDetectCellType(true);
        config.setIgnoreGraphics(false);

        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setConfiguration(config);
        return exportWithStream(exporter, jasperPrint);
    }

    private byte[] exportToXlsx(JasperPrint jasperPrint) throws JRException {
        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        config.setDetectCellType(true);
        config.setIgnoreGraphics(false);

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setConfiguration(config);
        return exportWithStream(exporter, jasperPrint);
    }

    private byte[] exportToPdf(JasperPrint jasperPrint) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        return exportWithStream(exporter, jasperPrint);
    }

    private byte[] exportWithStream(Exporter exporter, JasperPrint jasperPrint) throws JRException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            SimpleOutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(out);
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(output);
            exporter.exportReport();
            return out.toByteArray();
        } catch (IOException e) {
            throw new JRException("Ошибка при экспортировании отчета", e);
        }
    }

}
