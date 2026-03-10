package com.example.rces.spm.controller;

import com.example.rces.spm.services.SPMService;
import com.example.rces.spm.services.service.SPMReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
@RequestMapping("/report")
public class SPMReportController {

    private final SPMService spmService;
    private final SPMReportService SPMReportService;

    @Autowired
    public SPMReportController(SPMService spmService, SPMReportService SPMReportService) {
        this.spmService = spmService;
        this.SPMReportService = SPMReportService;
    }

    @GetMapping
    public String getReportForm() {
        return "report/tree";
    }

    @GetMapping("/print")
    public ResponseEntity<Resource> uploadToExcel(@RequestParam Long customerOrderId) {
        try {
            ByteArrayResource resource = SPMReportService.makeCustomerOrderTreeReport(customerOrderId);
            String filename = String.format("Дерево_ЗК_от_%s.xlsx", LocalDate.now());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8))
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

    }
}
