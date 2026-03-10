package com.example.rces.service;

import com.example.rces.dto.*;
import com.example.rces.models.NTDocument;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StateStandardDocumentService {

    List<NTDocumentDTO> getAllNTDocuments();

    NTDocumentDTO createNtDocument(NTDocumentCreateDTO dto);

    NTDocumentDTO updateNtDocument(UUID id, Long version, Map<String, Object> changes);

    NTDocument findById(UUID id);

    DocumentDTO createDocumentForNTD(NTDocument ntd, DocumentCreateDTO dto, Object file);

    void deleteNtd(UUID id);

    List<NTDocumentReferenceDTO> getAllReferences(UUID id, List<UUID> ids);

    List<NTDocumentReferenceDTO> getReferences(List<UUID> ids);

    void addReference(UUID id, UUID referenceId);

    void removeReference(UUID id, UUID referenceId);

    void calculateReferences(UUID id);

}
