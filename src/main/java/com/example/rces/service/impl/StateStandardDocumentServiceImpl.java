package com.example.rces.service.impl;

import com.example.rces.dto.*;
import com.example.rces.mapper.NTDocumentsMapper;
import com.example.rces.models.Document;
import com.example.rces.models.NTDocument;
import com.example.rces.models.enums.Color;
import com.example.rces.repository.StateStandardDocumentRepository;
import com.example.rces.service.DocumentService;
import com.example.rces.service.StateStandardDocumentService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class StateStandardDocumentServiceImpl implements StateStandardDocumentService {

    private final StateStandardDocumentRepository repository;
    private final NTDocumentsMapper mapper;
    private final ObjectMapper objectMapper;
    private final DocumentService documentService;

    @Autowired
    public StateStandardDocumentServiceImpl(StateStandardDocumentRepository repository, NTDocumentsMapper mapper, ObjectMapper objectMapper, DocumentService documentService) {
        this.repository = repository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.documentService = documentService;
    }

    @Override
    public List<NTDocumentDTO> getAllNTDocuments() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(NTDocument::getName).thenComparing(NTDocument::getType))
                .map(mapper::toDTO).toList();
    }

    @Override
    public NTDocumentDTO createNtDocument(NTDocumentCreateDTO dto) {
        NTDocument newNtd = mapper.toEntityFromCreateDTO(dto);
        NTDocument savedNtd = repository.save(newNtd);
        return mapper.toDTO(savedNtd);
    }

    @Override
    public NTDocumentDTO updateNtDocument(UUID id, Long version, Map<String, Object> changes) {
        NTDocument ntdEntity = repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("NTD с id %s не найдено", id)));
        if (!Objects.equals(ntdEntity.getVersion(), version)) {
            throw new OptimisticLockException("NTD с id " + id + " устарел");
        }
        try {
            objectMapper.updateValue(ntdEntity, changes);
        } catch (JsonMappingException e) {
            throw new ApplicationContextException("Ошибка при обновлении NTD", e);
        }
        ntdEntity.setColor(Color.NONE);
        repository.save(ntdEntity);

        ntdEntity.setVersion(ntdEntity.getVersion() + 1);
        return mapper.toDTO(ntdEntity);
    }

    @Override
    public NTDocument findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("NTD не найден"));
    }

    @Override
    public DocumentDTO createDocumentForNTD(NTDocument ntd, DocumentCreateDTO dto, Object file) {
        dto.setName(String.format("Документация на %s %s", ntd.getName(), LocalDateTime.now()));
        Document document;
        if (file != null || dto.getFiles() != null) {
            document = documentService.createDocument(dto, file != null ? file : dto.getFiles());
        } else {
            document = documentService.createDocument(dto);
        }
        ntd.setDocument(document);
        return documentService.toDTO(document);
    }

    @Override
    public void deleteNtd(UUID id) {
        NTDocument ntd = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("NTD не найден"));
        if (ntd.getDocument() != null) {
            documentService.deleteDocument(ntd.getDocument().getId());
        }
        ntd.getReferences().clear();
        repository.deleteById(id);
    }

    @Override
    public List<NTDocumentReferenceDTO> getAllReferences(UUID id, List<UUID> ids) {
        List<NTDocument> references;
        if (ids!=null) {
            ids.add(id);
            references = repository.findAllWithoutIds(ids);
        } else {
            references = repository.findAllWithoutIds(List.of(id));
        }
        return references.stream().map(mapper::toReferenceDTO).toList();
    }

    @Override
    public List<NTDocumentReferenceDTO> getReferences(List<UUID> ids) {
        List<NTDocument> references = repository.findAllById(ids);
        return references.stream().map(mapper::toReferenceDTO).toList();
    }

    @Override
    public void addReference(UUID ntdId, UUID referenceId) {
        NTDocument ntd = repository.findById(ntdId).orElseThrow(() -> new EntityNotFoundException("NTD не найден"));
        NTDocument reference = repository.findById(referenceId).orElseThrow(() -> new EntityNotFoundException("NTD reference не найден"));
        ntd.getReferences().add(reference);
        repository.save(ntd);
    }

    @Override
    public void removeReference(UUID ntdId, UUID referenceId) {
        NTDocument ntd = repository.findById(ntdId).orElseThrow(() -> new EntityNotFoundException("NTD не найден"));
        NTDocument reference = repository.findById(referenceId).orElseThrow(() -> new EntityNotFoundException("NTD reference не найден"));
        ntd.getReferences().remove(reference);
        repository.save(ntd);
    }

    @Override
    public void calculateReferences(UUID id) {
        NTDocument ntd = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("NTD не найден"));
        ntd.getReferences().forEach(ref -> ref.setColor(Color.RED));
        ntd.setColor(Color.NONE);
        repository.save(ntd);
    }
}
