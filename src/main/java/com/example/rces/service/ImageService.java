package com.example.rces.service;

import com.example.rces.dto.ImagesDTO;
import com.example.rces.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    void save(Images newImage);

    void deleteById(UUID imageId);

    List<Images> createImages(MultipartFile[] additionalFiles, Requests request, Boolean save);

    List<Images> createImages(MultipartFile[] additionalFiles, SGI sgi, Boolean save);

    List<Images> createImages(MultipartFile[] additionalFiles, FactExecutionSGI factExecutionSGI, Boolean save);

    List<Images> createImages(MultipartFile[] additionalFiles, InspectionViolation request, Boolean save);

    List<ImagesDTO> getImagesByRequestId(UUID param);

    List<ImagesDTO> getImagesForSgiId(UUID sgiId);

    List<ImagesDTO> getImagesForFactSgiId(UUID factsSgiId);

    List<ImagesDTO> getImagesForInspectionId(UUID inspectionId);

    List<Images> findAllByIds(List<UUID> imageIds);

}
