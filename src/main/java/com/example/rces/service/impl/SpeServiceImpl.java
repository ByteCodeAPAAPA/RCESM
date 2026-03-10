package com.example.rces.service.impl;

import com.example.rces.dto.*;
import com.example.rces.mapper.SPEMapper;
import com.example.rces.models.Document;
import com.example.rces.models.SPE;
import com.example.rces.models.enums.OrganizationSPE;
import com.example.rces.models.enums.StatusSPE;
import com.example.rces.repository.SpeRepository;
import com.example.rces.service.DocumentService;
import com.example.rces.service.ReportService;
import com.example.rces.service.SpeService;
import com.example.rces.utils.ApiClient;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.rces.utils.ServiceUtil.colorCalculate;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class SpeServiceImpl implements SpeService {

    private final SpeRepository repository;
    private final DocumentService documentService;
    private final SPEMapper mapper;
    private final ObjectMapper objectMapper;
    private final ReportService reportService;
    private final ApiClient apiClient;

    @Autowired
    public SpeServiceImpl(SpeRepository repository, DocumentService documentService, SPEMapper mapper, ObjectMapper objectMapper, ReportService reportService, ApiClient apiClient) {
        this.repository = repository;
        this.documentService = documentService;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.reportService = reportService;
        this.apiClient = apiClient;
    }

    @Override
    public SpeDTO createSPE(SpeFgisCreateDTO dto) {
        JsonNode fgisData = apiClient.getFgisData(dto.getOutNumber());
        SpeCreateDTO createDTO = new SpeCreateDTO(fgisData, dto);
        SPE newSPE = mapper.toEntityFromCreateDTO(createDTO);
        newSPE.setStatus(calculateStatus(newSPE));
        newSPE.setColor(colorCalculate(newSPE));
        this.createSpeDocument(newSPE, new DocumentCreateDTO(), reportService.createSpeFgisReport(fgisData));
        SPE savedSpe = repository.save(newSPE);
        return mapper.toDTO(savedSpe);
    }


    @Override
    public SpeDTO createSPE(SpeCreateDTO dto) {
        SPE newSPE = mapper.toEntityFromCreateDTO(dto);
        newSPE.setStatus(calculateStatus(newSPE));
        newSPE.setColor(colorCalculate(newSPE));
        SPE savedSpe = repository.save(newSPE);
        return mapper.toDTO(savedSpe);
    }

    @Override
    public SPE findByNumber(Integer number) {
        return repository.findById(number).orElseThrow(() -> new EntityNotFoundException("SPE не найден"));
    }

    @Override
    public List<SpeDTO> getAllSPE() {
        var allSPE = repository.findAll();
        return allSPE.stream().map(mapper::toDTO).toList();
    }

    @Override
    public SpeDTO updateSPE(Integer number, Long version, Map<String, Object> changes) {
        SPE speEntity = repository.findById(number).orElseThrow(
                () -> new EntityNotFoundException(String.format("Spe с id %s не найдено", number)));
        if (!Objects.equals(speEntity.getVersion(), version)) {
            throw new OptimisticLockException("SPE с id " + number + " устарел");
        }
        try {
            objectMapper.updateValue(speEntity, changes);
        } catch (JsonMappingException e) {
            throw new ApplicationContextException("Ошибка при обновлении SPE", e);
        }
        if (speEntity.getMark() != null) {
            switch (speEntity.getMark()) {
                case "исправен" -> speEntity.setStatus(StatusSPE.CORRECTED);
                case "списан" -> speEntity.setStatus(StatusSPE.WRITE_OFF);
                case "на поверке" -> speEntity.setStatus(StatusSPE.AT_INSPECTION);
                case "ремонт" -> speEntity.setStatus(StatusSPE.REPAIR);
            }
        }
        speEntity.setStatus(calculateStatus(speEntity));
        speEntity.setColor(colorCalculate(speEntity));
        if (speEntity.getStatus() == StatusSPE.EXPIRED || speEntity.getStatus() == StatusSPE.VERIFICATION_REQUIRED) {
            speEntity.setMark(null);
        }
        repository.save(speEntity);
        speEntity.setVersion(speEntity.getVersion() + 1);
        return mapper.toDTO(speEntity);
    }

    @Override
    public void deleteSpe(Integer number) {
        SPE spe = repository.findById(number).orElseThrow(() -> new EntityNotFoundException("SPE не найден"));
        if (spe.getDocument() != null) {
            documentService.deleteDocument(spe.getDocument().getId());
        }
        repository.deleteById(number);
    }

    @Override
    public List<SPE> findAllByIdList(List<Integer> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public DocumentDTO createSpeDocument(SPE spe, DocumentCreateDTO dto, Object file) {
        dto.setName(String.format("Инструмент %s сертификат %s %s", spe.getName(), spe.getCertificateNumber(), LocalDateTime.now()));
        Document document;
        if (file != null || dto.getFiles()!=null) {
            document = documentService.createDocument(dto, file!=null ? file : dto.getFiles());
        } else {
            document = documentService.createDocument(dto);
        }
        spe.setDocument(document);
        return documentService.toDTO(document);
    }

    @Override
    public void setOrganizationWithFgis(SPE spe, JsonNode data) {
        String organizationName = data.path("vriInfo").path("organization").asText();
        OrganizationSPE organization = OrganizationSPE.fromString(organizationName);
        spe.setOrganization(organization);
        repository.save(spe);
    }

    @Override
    public void calculateDateVerification() {
        List<SPE> speList = repository.findAll();
        speList.forEach(spe -> {
            spe.setStatus(calculateStatus(spe));
            if (spe.getStatus() == StatusSPE.EXPIRED || spe.getStatus() == StatusSPE.VERIFICATION_REQUIRED) {
                spe.setMark(null);
            }
            spe.setColor(colorCalculate(spe));
            repository.save(spe);
        });
    }

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void notifyExpiredDeviations() {
        calculateDateVerification();
    }

    private StatusSPE calculateStatus(SPE spe) {
        StatusSPE currentStatus = spe.getStatus();
        if (currentStatus == StatusSPE.WRITE_OFF || currentStatus == StatusSPE.AT_INSPECTION || currentStatus == StatusSPE.REPAIR) {
            return currentStatus;
        } else if (spe.getDateVerification()!=null && LocalDate.now().isAfter(spe.getDateVerification())) {
            return StatusSPE.EXPIRED;
        } else if (spe.getDateVerification()!=null && (ChronoUnit.MONTHS.between(LocalDate.now(), spe.getDateVerification()) == 0 ||
                ChronoUnit.MONTHS.between(LocalDate.now(), spe.getDateVerification()) == 1)) {
            return StatusSPE.VERIFICATION_REQUIRED;
        } else {
            return currentStatus;
        }
    }

}
