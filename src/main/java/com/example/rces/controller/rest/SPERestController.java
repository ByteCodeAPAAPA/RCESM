package com.example.rces.controller.rest;

import com.example.rces.dto.*;
import com.example.rces.service.SpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Map;

@RestController
@RequestMapping("/api/spe")
public class SPERestController {

    private final SpeService service;

    @Autowired
    public SPERestController(SpeService service) {
        this.service = service;
    }

    @PostMapping("/create-spe-fgis")
    public ResponseEntity<SpeDTO> createSPE(@RequestBody SpeFgisCreateDTO dto) {
        var newSpe = service.createSPE(dto);
        return ResponseEntity.ok(newSpe);
    }

    @PostMapping("/create-spe")
    public ResponseEntity<SpeDTO> createSPE(@RequestBody SpeCreateDTO dto) {
        var newSpe = service.createSPE(dto);
        return ResponseEntity.ok(newSpe);
    }

    @GetMapping("/get-page-spe")
    public ResponseEntity<RequestDataDTO> getPage() {
        var allSpeList = service.getAllSPE().stream().sorted(Comparator.comparing(SpeDTO::getId)).toList();
        return ResponseEntity.ok(new RequestDataDTO(allSpeList, allSpeList.size()));
    }

    @PatchMapping("/update/{number}")
    public ResponseEntity<SpeDTO> update(@PathVariable Integer number,
                                         @RequestParam Long version,
                                         @RequestBody Map<String, Object> changes) {
        var updatedSPE = service.updateSPE(number, version, changes);
        return ResponseEntity.ok(updatedSPE);
    }

    @PostMapping("/create-document/{number}")
    public ResponseEntity<DocumentDTO> createDocument(@PathVariable Integer number, @ModelAttribute DocumentCreateDTO dto) {
        return ResponseEntity.ok(service.createSpeDocument(service.findByNumber(number), dto, null));
    }

    @DeleteMapping("/delete/{number}")
    public ResponseEntity<Void> delete(@PathVariable Integer number) {
        service.deleteSpe(number);
        return ResponseEntity.ok().build();
    }

}
