package com.example.rces.controller.rest;

import com.example.rces.dto.FileDTO;
import com.example.rces.models.SGI;
import com.example.rces.models.enums.Format;
import com.example.rces.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.example.rces.utils.DateUtil.formatedDate;

@RestController
@RequestMapping("/api/report")
public class ReportRestController {

    private final ReportService service;

    @Autowired
    public ReportRestController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/print/sgi")
    public ResponseEntity<Resource> printManySgi(@RequestParam(required = false) List<UUID> ids, @RequestParam(required = false) String department) {
        try {
            List<SGI> sgiList = service.getSgiList(ids, department);
            ByteArrayResource resource = service.getExcelFile(sgiList);
            String filename = (department != null ? "Не_выполненные_мероприятия_" : "Мероприятия_") + formatedDate(LocalDate.now()) + ".docx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                    .replace("+", "%20");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/print/bid")
    public ResponseEntity<byte[]> printBid() throws IOException {
        byte[] report = service.reportBid();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("rejected_bids_.xlsx")
                .build());
        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping("/print/spe")
    public ResponseEntity<FileDTO> printSPE(@RequestParam(name = "format") String formatString, @RequestParam List<Integer> idList) {
        Format format = Format.valueOf(formatString);
        byte[] report = service.createSpeReport(format, idList);
        String fileName = String.format("Извещение_о_предъявлении_СИ_на_поверку_ОТК_от_%s.%s",
                formatedDate(LocalDate.now()), format.getFileExtension());
        FileDTO fileDTO = new FileDTO(fileName, report);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileDTO);
    }

    @GetMapping("/print/spe-schedule")
    public ResponseEntity<FileDTO> printSpeSchedule(@RequestParam(name = "format") String formatString, @RequestParam List<Integer> idList) {
        Format format = Format.valueOf(formatString);
        byte[] report = service.createSpeSchedule(format, idList);
        String fileName = String.format("График_поверки_от_%s.%s",
                formatedDate(LocalDateTime.now()), format.getFileExtension());
        FileDTO fileDTO = new FileDTO(fileName, report);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileDTO);
    }

    @GetMapping("/print/inspection-workshop")
    public ResponseEntity<FileDTO> printInspectionWorkshop(@RequestParam(name = "format") String formatString, @RequestParam Integer id) {
        Format format = Format.valueOf(formatString);
        byte[] report = service.createInspectionReport(format, id);
        String fileName = String.format("Инспекция_от_%s.%s",
                formatedDate(LocalDateTime.now()), format.getFileExtension());
        FileDTO fileDTO = new FileDTO(fileName, report);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileDTO);
    }

    @GetMapping("/print/inspection-services")
    public ResponseEntity<FileDTO> printInspectionServices(@RequestParam(name = "format") String formatString) {
        Format format = Format.valueOf(formatString);
        byte[] report = service.createInspectionReport(format, null);
        String fileName = String.format("Инспекция_службы_от_%s.%s",
                formatedDate(LocalDateTime.now()), format.getFileExtension());
        FileDTO fileDTO = new FileDTO(fileName, report);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fileDTO);
    }

}
