package com.example.rces.service;

import com.example.rces.dto.InspectionCreateDTO;
import com.example.rces.dto.InspectionDTO;
import com.example.rces.dto.InspectionViolationCreateDTO;
import com.example.rces.dto.InspectionViolationDTO;
import com.example.rces.models.InspectionViolation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface InspectionService {

    List<InspectionDTO> getAllInspection();

    List<InspectionViolationDTO> getViolationsForInspectionId(Integer id);

    List<InspectionViolationDTO> getAllViolationServices();

    InspectionDTO createInspection(InspectionCreateDTO dto);

    InspectionDTO createSecondaryInspection(Integer inspectionId);

    InspectionViolationDTO createViolation(InspectionViolationCreateDTO dto, MultipartFile[] additionalFiles);

    String changeStatus(UUID id);

    void deleteInspection(Integer id);

    void deleteInspectionViolation(UUID id);
}
