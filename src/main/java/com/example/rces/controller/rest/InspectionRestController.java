package com.example.rces.controller.rest;

import com.example.rces.dto.*;
import com.example.rces.service.ImageService;
import com.example.rces.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inspection")
public class InspectionRestController {

    private final InspectionService service;
    private final ImageService imageService;

    @Autowired
    public InspectionRestController(InspectionService inspectionService, ImageService imageService) {
        this.service = inspectionService;
        this.imageService = imageService;
    }

    @GetMapping("/get-page-inspection")
    public ResponseEntity<RequestDataDTO> getPage() {
        var allSpeList = service.getAllInspection();
        return ResponseEntity.ok(new RequestDataDTO(allSpeList, allSpeList.size()));
    }

    @GetMapping("/get-violation/{id}")
    public ResponseEntity<List<InspectionViolationDTO>> getViolationsForInspectionId(@PathVariable Integer id) {
        List<InspectionViolationDTO> violation = service.getViolationsForInspectionId(id);
        return ResponseEntity.ok().body(violation);
    }

    @GetMapping("/get-images-inspection/{id}")
    public ResponseEntity<List<ImagesDTO>> getImagesForInspectionId(@PathVariable UUID id) {
        List<ImagesDTO> imagesPayload = imageService.getImagesForInspectionId(id);
        return ResponseEntity.ok().body(imagesPayload);
    }

    @GetMapping("/get-all-services-violation")
    public ResponseEntity<List<InspectionViolationDTO>> getAllViolationServices() {
        List<InspectionViolationDTO> violationDTOList = service.getAllViolationServices();
        return ResponseEntity.ok(violationDTOList);
    }

    @PostMapping("/create-inspection")
    public ResponseEntity<InspectionDTO> createInspection(@RequestBody InspectionCreateDTO dto) {
        var newInspection = service.createInspection(dto);
        return ResponseEntity.ok(newInspection);
    }

    @PostMapping("/create-secondary-inspection/{inspectionId}")
    public ResponseEntity<InspectionDTO> createSecondaryInspection(@PathVariable Integer inspectionId) {
        var newInspection = service.createSecondaryInspection(inspectionId);
        return ResponseEntity.ok(newInspection);
    }

    @PostMapping("/create-violation")
    public ResponseEntity<InspectionViolationDTO> createViolation(@RequestPart("data") InspectionViolationCreateDTO dto,
                                                                  @RequestPart(value = "additionalFiles", required = false) MultipartFile[] additionalFiles) {
        var newViolation = service.createViolation(dto, additionalFiles);
        return ResponseEntity.ok(newViolation);
    }

    @PatchMapping("/change-status-violation/{id}")
    public ResponseEntity<String> changeStatus(@PathVariable UUID id) {
        String status = service.changeStatus(id);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/delete-inspection/{id}")
    public ResponseEntity<Void> deleteInspection(@PathVariable Integer id) {
        service.deleteInspection(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-violation/{id}")
    public ResponseEntity<Void> deleteInspectionViolation(@PathVariable UUID id) {
        service.deleteInspectionViolation(id);
        return ResponseEntity.noContent().build();
    }

}
