package com.example.rces.controller.rest;

import com.example.rces.dto.MachineCreateDto;
import com.example.rces.dto.MachineDto;
import com.example.rces.models.DocumentFile;
import com.example.rces.service.DocumentService;
import com.example.rces.service.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/machines")
public class MachineRestController {

    private final MachineService machineService;
    private final DocumentService documentService;

    @Autowired
    public MachineRestController(MachineService machineService, DocumentService documentService) {
        this.machineService = machineService;
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<List<MachineDto>> getAllMachines() {
        List<MachineDto> machines = machineService.getAllMachine();
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/{number}")
    public ResponseEntity<MachineDto> getMachineByNumber(@PathVariable Integer number) {
        MachineDto machine = machineService.findByNumber(number);
        return ResponseEntity.ok(machine);
    }

    @PostMapping
    public ResponseEntity<MachineDto> createMachine(@ModelAttribute MachineCreateDto createDto) {
        MachineDto newMachine = machineService.createMachine(createDto);
        return ResponseEntity.ok(newMachine);
    }

    @PutMapping("/{number}")
    public ResponseEntity<MachineDto> updateMachine(@PathVariable Integer number, @RequestBody MachineDto machineDto) {
        machineDto.setNumber(number);
        MachineDto updatedMachine = machineService.updateMachine(machineDto);
        return ResponseEntity.ok(updatedMachine);
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<Void> deleteMachine(@PathVariable Integer number) {
        machineService.deleteMachineByNumber(number);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{number}/documents")
    public ResponseEntity<Void> addDocumentsToMachine(@PathVariable Integer number, @RequestParam("files") MultipartFile[] files) {
        machineService.addPdfToMachine(number, files);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{number}/photos")
    public ResponseEntity<Void> addPhotosToMachine(@PathVariable Integer number, @RequestParam("photos") MultipartFile[] photos) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<byte[]> getDocument(@PathVariable UUID id) {
        DocumentFile documentFile = documentService.getDocumentFileById(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ContentDisposition disposition = ContentDisposition
                .builder("inline")
                .filename(documentFile.getBaseFileName(), StandardCharsets.UTF_8)
                .build();

        headers.setContentDisposition(disposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(documentFile.getContent());
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteFileFromDocument(id);
        return ResponseEntity.noContent().build();
    }

}