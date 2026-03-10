package com.example.rces.controller.rest;

import com.example.rces.dto.InconsistencyCreateDto;
import com.example.rces.dto.InconsistencyDto;
import com.example.rces.service.InconsistenciesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inconsistencies")
@Validated
public class InconsistenciesRestController {

    InconsistenciesService inconsistenciesService;

    @Autowired
    public InconsistenciesRestController(InconsistenciesService inconsistenciesService) {
        this.inconsistenciesService = inconsistenciesService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody InconsistencyCreateDto dto) {
        try {
            InconsistencyDto inconsistency = inconsistenciesService.createInconsistency(dto);
            return ResponseEntity.ok(inconsistency);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
