package com.example.rces.controller.rest;

import com.example.rces.dto.*;
import com.example.rces.models.SGI;
import com.example.rces.service.ImageService;
import com.example.rces.service.SgiService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.example.rces.utils.ServiceUtil.colorCalculate;

@RestController
@RequestMapping("/api/sgi")
public class SGIRestController {

    private final SgiService sgiService;
    private final ImageService imageService;

    @Autowired
    public SGIRestController(SgiService sgiService, ImageService imageService) {
        this.sgiService = sgiService;
        this.imageService = imageService;
    }

    @PostMapping(value = "/create-sgi")
    public ResponseEntity<SgiDTO> createSGI(@RequestPart("data") SgiCreateDTO dto,
                                            @RequestPart(value = "additionalFiles", required = false) MultipartFile[] additionalFiles) {
        SgiDTO newSGi = sgiService.createSGI(dto, additionalFiles);
        return ResponseEntity.ok(newSGi);
    }

    @PatchMapping("/update")
    public ResponseEntity<SgiDTO> updateSGI(@RequestParam UUID id,
                                            @RequestParam(required = false) String workcenter,
                                            @RequestParam(required = false) String event,
                                            @RequestParam(required = false) String actions,
                                            @RequestParam(required = false) String department,
                                            @RequestParam(required = false) LocalDate desiredDate,
                                            @RequestParam(required = false) LocalDate planDate,
                                            @RequestParam(required = false) String employee,
                                            @RequestParam(required = false) String note,
                                            @RequestParam Boolean factExecutionSGIBool,
                                            @RequestParam(required = false) LocalDate executionDate,
                                            @RequestParam(required = false) String report,
                                            @RequestParam(required = false) MultipartFile[] imagesSGI,
                                            @RequestParam(required = false) MultipartFile[] imagesFactSGI) throws CloneNotSupportedException {
        SGI sgi = sgiService.findById(id).orElseThrow(() -> new ApplicationContextException("Передан null в id SGI на сохранение изменений"));
        SgiDTO updateSGI = sgiService.save(sgi, workcenter, event, actions, department, desiredDate, planDate,
                employee, note, executionDate, factExecutionSGIBool, executionDate, report, imagesSGI, imagesFactSGI);
        return ResponseEntity.ok(updateSGI);
    }

    @PatchMapping("/agree")
    public ResponseEntity<?> coordination(@RequestParam UUID id, @RequestParam Boolean agreed) {
        SGI sgi = sgiService.findById(id).orElseThrow(() -> new ApplicationContextException("Передан null в id SGI на согласование"));
        try {
            sgi = sgiService.save(sgi, agreed);
            return ResponseEntity.ok(sgi.getAgreed());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/calculate-color")
    public ResponseEntity<Void> reCalculateColor() {
        LocalDate today = LocalDate.now();
        List<SGI> sgiList = sgiService.findAll();
        for (SGI sgi : sgiList) {
            sgi.setColor(colorCalculate(sgi, today));
        }
        sgiService.saveAll(sgiList);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-page-sgi")
    public ResponseEntity<RequestDataDTO> getPageSGI(@RequestParam(required = false, defaultValue = "1") int page,
                                                     @RequestParam(required = false, defaultValue = "16") int size) {
        Page<SgiDTO> pageSgiPayload = sgiService.getPage(page, size);
        return ResponseEntity.ok().body(new RequestDataDTO(pageSgiPayload.getContent(), pageSgiPayload.getTotalElements()));
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<SgiDTO> getById(@PathVariable UUID id) {
        SgiDTO sgiDTO = sgiService.getById(id);
        return ResponseEntity.ok(sgiDTO);
    }

    @GetMapping("/get-images-sgi")
    public ResponseEntity<List<ImagesDTO>> getImagesForSgiId(@RequestParam UUID id) {
        List<ImagesDTO> imagesPayload = imageService.getImagesForSgiId(id);
        return ResponseEntity.ok().body(imagesPayload);
    }

    @GetMapping("/get-images-fact-sgi")
    public ResponseEntity<List<ImagesDTO>> getImagesForFactSgiId(@RequestParam UUID id) {
        List<ImagesDTO> imagesPayload = imageService.getImagesForFactSgiId(id);
        return ResponseEntity.ok().body(imagesPayload);
    }

    @PostMapping("/create-document/{id}")
    public ResponseEntity<DocumentDTO> createDocument(@PathVariable UUID id, @ModelAttribute DocumentCreateDTO dto) {
        return ResponseEntity.ok(sgiService.createSpeDocument(sgiService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Нет SGI с id = " + id)), dto, null));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSGI(@PathVariable UUID id) {
        sgiService.delete(sgiService.findById(id).orElseThrow(
                () -> new ApplicationContextException("Передан null в списке на удаление SGI")));
        return ResponseEntity.noContent().build();
    }

}
