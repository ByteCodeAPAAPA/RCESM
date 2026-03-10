package com.example.rces.controller.rest;

import com.example.rces.dto.*;
import com.example.rces.service.StateStandardDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ntd")
public class NTDocumentRestController {

    private final StateStandardDocumentService service;

    @Autowired
    public NTDocumentRestController(StateStandardDocumentService service) {
        this.service = service;
    }

    @GetMapping("/get-page-ntd")
    public ResponseEntity<RequestDataDTO> getPage() {
        var allSpeList = service.getAllNTDocuments().stream().sorted(Comparator.comparing(NTDocumentDTO::getId)).toList();
        return ResponseEntity.ok(new RequestDataDTO(allSpeList, allSpeList.size()));
    }

    @PostMapping("/create-ntd")
    public ResponseEntity<NTDocumentDTO> createSPE(@RequestBody NTDocumentCreateDTO dto) {
        var newNtd = service.createNtDocument(dto);
        return ResponseEntity.ok(newNtd);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<NTDocumentDTO> update(@PathVariable UUID id,
                                                @RequestParam Long version,
                                                @RequestBody Map<String, Object> changes) {
        var updatedNtd = service.updateNtDocument(id, version, changes);
        return ResponseEntity.ok(updatedNtd);
    }

    @PostMapping("/create-document/{id}")
    public ResponseEntity<DocumentDTO> createDocument(@PathVariable UUID id, @ModelAttribute DocumentCreateDTO dto) {
        return ResponseEntity.ok(service.createDocumentForNTD(service.findById(id), dto, null));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteNtd(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all-references")
    public ResponseEntity<List<NTDocumentReferenceDTO>> getAllReferences(@RequestParam UUID id, @RequestParam(required = false) List<UUID> ids) {
        return ResponseEntity.ok(service.getAllReferences(id, ids));
    }

    @GetMapping("/get-references")
    public ResponseEntity<List<NTDocumentReferenceDTO>> getReferences(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(service.getReferences(ids));
    }

    @PatchMapping("/add-reference")
    public ResponseEntity<Void> addReference(@RequestParam UUID id, @RequestParam UUID referenceId) {
        service.addReference(id, referenceId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/remove-reference")
        public ResponseEntity<Void> removeReference(@RequestParam UUID id, @RequestParam UUID referenceId) {
        service.removeReference(id, referenceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate-references")
    public ResponseEntity<Void> calculateReferences(@RequestParam UUID id) {
        service.calculateReferences(id);
        return ResponseEntity.ok().build();
    }
}
