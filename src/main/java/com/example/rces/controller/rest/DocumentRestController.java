package com.example.rces.controller.rest;

import com.example.rces.dto.DocumentDTO;
import com.example.rces.dto.DocumentFileDTO;
import com.example.rces.dto.FileDTO;
import com.example.rces.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/document")
public class DocumentRestController {

    private final DocumentService documentService;

    @Autowired
    public DocumentRestController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/get-document/{id}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PatchMapping("/add-file-to-document/{id}")
    public ResponseEntity<List<DocumentFileDTO>> addFileToDocument(@PathVariable UUID id, @RequestParam List<MultipartFile> files) {
        return ResponseEntity.ok(documentService.addFileToDocument(id, files));
    }

    @PatchMapping("/add-file-2-document/{id}")
    public ResponseEntity<DocumentFileDTO> addFileToDocument(@PathVariable UUID id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(documentService.addFileToDocument(id, file));
    }

    @PatchMapping("/add-file-to-document-and-get/{id}")
    public ResponseEntity<DocumentDTO> addFileToDocumentAndGet(@PathVariable UUID id, @RequestParam List<MultipartFile> files) {
        return ResponseEntity.ok(documentService.addFileToDocumentAndGet(id, files));
    }

    @PatchMapping("/add-file-2-document-and-get/{id}")
    public ResponseEntity<DocumentDTO> addFileToDocumentAndGet(@PathVariable UUID id, @RequestParam MultipartFile file) {
        return ResponseEntity.ok(documentService.addFileToDocumentAndGet(id, file));
    }

    @DeleteMapping("/delete-file-from-document/{id}")
    public ResponseEntity<Void> deleteFileFromDocument(@PathVariable UUID id) {
        documentService.deleteFileFromDocument(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-document/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download-document-file/{id}")
    public ResponseEntity<FileDTO> downloadFile(@PathVariable UUID id) {
        FileDTO file = documentService.downloadFile(id);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/download-all-document-file/{documentId}")
    public ResponseEntity<List<FileDTO>> downloadAllFile(@PathVariable UUID documentId) {
        List<FileDTO> files = documentService.downloadAllFile(documentId);
        return ResponseEntity.ok(files);
    }
}
