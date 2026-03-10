package com.example.rces.service;

import com.example.rces.dto.DocumentCreateDTO;
import com.example.rces.dto.DocumentDTO;
import com.example.rces.dto.DocumentFileDTO;
import com.example.rces.dto.FileDTO;
import com.example.rces.models.Document;
import com.example.rces.models.DocumentFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    Document createDocument(DocumentCreateDTO dto);

    Document createDocument(DocumentCreateDTO dto, Object file);

    DocumentDTO toDTO(Document document);

    DocumentDTO getDocumentById(UUID id);

    List<DocumentFileDTO> addFileToDocument(UUID documentId, List<MultipartFile> files);

    DocumentFileDTO addFileToDocument(UUID documentId, MultipartFile file);

    DocumentDTO addFileToDocumentAndGet(UUID documentId, List<MultipartFile> file);

    DocumentDTO addFileToDocumentAndGet(UUID documentId, MultipartFile file);

    void deleteFileFromDocument(UUID fileId);

    void deleteDocument(UUID id);

    FileDTO downloadFile(UUID fileId);

    List<FileDTO> downloadAllFile(UUID documentId);

    DocumentFile getDocumentFileById(UUID id);
}
