package com.example.rces.repository;

import com.example.rces.models.Images;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository  extends JpaRepository<Images, UUID> {

    List<Images> findAllByRequestId(UUID requestId);

    List<Images> findAllBySgimId(UUID sgiId);

    List<Images> findAllBySgiId(UUID sgiId);

    List<Images> findAllByInsVioId(UUID inspectionId);
}
