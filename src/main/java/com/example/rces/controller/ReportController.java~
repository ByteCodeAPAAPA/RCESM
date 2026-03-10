package com.example.rces.controller;

import com.example.rces.models.SGI;
import com.example.rces.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.example.rces.utils.DateUtil.formatedDate;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService service;

    @Autowired
    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/print/sgi")
    public ResponseEntity<Resource> printManySgi(@RequestParam(required = false) List<UUID> ids, @RequestParam(required = false) String department) {
        try {
            List<SGI> sgiList = service.getSgiList(ids, department);
            ByteArrayResource resource = service.getExcelFile(sgiList);

            String filename = (department != null ? "Не_выполненные_мероприятия_" : "Мероприятия_") + formatedDate(LocalDate.now()) + ".docx";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString())
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("rejected_bids_.xlsx")
                .build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

}
