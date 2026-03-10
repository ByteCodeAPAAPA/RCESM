package com.example.rces.service.impl;

import com.example.rces.dto.ImagesDTO;
import com.example.rces.dto.RequestHistoryDTO;
import com.example.rces.models.BaseRevisionEntity;
import com.example.rces.models.Requests;
import com.example.rces.models.enums.Status;
import com.example.rces.service.ImageService;
import com.example.rces.service.RequestHistoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class RequestHistoryServiceImpl implements RequestHistoryService {

    private final ImageService imageService;

    @PersistenceContext
    private EntityManager em;

    public RequestHistoryServiceImpl(ImageService imageService) {
        this.imageService = imageService;
    }

    private AuditReader getAuditReader() {
        return AuditReaderFactory.get(em);
    }

    public List<RequestHistoryDTO> getDetailedRequestHistory(UUID requestId) {
        AuditReader reader = getAuditReader();

        try {
            @SuppressWarnings("unchecked")
            List<Object[]> revisions = reader.createQuery()
                    .forRevisionsOfEntity(Requests.class, false, true)
                    .add(AuditEntity.id().eq(requestId))
                    .addOrder(AuditEntity.revisionNumber().asc())
                    .getResultList();

            List<RequestHistoryDTO> history = new ArrayList<>();

            for (Object[] revisionData : revisions) {
                BaseRevisionEntity revisionEntity = (BaseRevisionEntity) revisionData[1];

                Set<String> inconsistencies = getInconsistenciesForRevisionNative(requestId, revisionEntity.getRev());

                RequestHistoryDTO dto = mapToRequestHistoryDTO(revisionData);

                dto.getRequestData().setInconsistencies(inconsistencies);

                history.add(dto);
            }

            return history;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении истории заявки: " + e.getMessage(), e);
        }
    }

    @Override
    public List<RequestHistoryDTO> getRequestHistory(UUID requestId) {
        AuditReader reader = getAuditReader();

        try {
            @SuppressWarnings("unchecked")
            List<Object[]> revisions = reader.createQuery()
                    .forRevisionsOfEntity(Requests.class, false, true)
                    .add(AuditEntity.id().eq(requestId))
                    .addOrder(AuditEntity.revisionNumber().asc())
                    .getResultList();

            List<RequestHistoryDTO> history = new ArrayList<>();
            List<ImagesDTO> allImages = imageService.getImagesByRequestId(requestId);

            List<Object[]> rejectedRevisions = new ArrayList<>();
            for (Object[] revisionData : revisions) {
                Requests request = (Requests) revisionData[0];
                if (request.getStatus().equals(Status.Rejected)) {
                    rejectedRevisions.add(revisionData);
                }
            }

            for (int i = 0; i < rejectedRevisions.size(); i++) {
                Object[] revisionData = rejectedRevisions.get(i);
                BaseRevisionEntity revisionEntity = (BaseRevisionEntity) revisionData[1];
                Requests request = (Requests) revisionData[0];

                Set<String> inconsistencies = getInconsistenciesForRevisionNative(requestId, revisionEntity.getRev() - 1);

                RequestHistoryDTO dto = mapToRequestHistoryDTO(revisionData);
                dto.getRequestData().setInconsistencies(inconsistencies);

                BaseRevisionEntity previousRevisionEntity = null;
                if (i > 0) {

                    previousRevisionEntity = (BaseRevisionEntity) rejectedRevisions.get(i - 1)[1];
                }

                List<ImagesDTO> periodImages;
                if (i == 0) {

                    periodImages = getImagesBeforeRevision(allImages, revisionEntity);
                } else {
                    periodImages = getImagesBetweenRevisions(allImages, previousRevisionEntity, revisionEntity);
                }

                dto.getRequestData().setImages(periodImages);
                history.add(dto);
            }

            return history;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении заявки: " + e.getMessage(), e);
        }
    }

    private RequestHistoryDTO mapToRequestHistoryDTO(Object[] revisionData) {
        Requests request = (Requests) revisionData[0];
        BaseRevisionEntity revisionEntity = (BaseRevisionEntity) revisionData[1];
        RevisionType revisionType = (RevisionType) revisionData[2];

        RequestHistoryDTO dto = new RequestHistoryDTO();
        dto.setRequestId(request.getId());
        dto.setRequestData(RequestHistoryDTO.RequestHistoryData.from(request));
        dto.setRevisionNumber(revisionEntity.getRev());
        dto.setRevisionDate(Instant.ofEpochMilli(revisionEntity.getRevtstmp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        dto.setRevisionType(revisionType.name());
        dto.setChangedBy(revisionEntity.getChangedBy().getName());

        return dto;
    }

    private List<ImagesDTO> getImagesBeforeRevision(List<ImagesDTO> images, BaseRevisionEntity revision) {
        List<ImagesDTO> revisionImages = new ArrayList<>();

        LocalDateTime revisionDate = Instant.ofEpochMilli(revision.getRevtstmp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        for (ImagesDTO image : images) {
            if (image.getName().contains("Фото")) {
                String dateString = image.getName().substring(7).strip();
                LocalDateTime dateImages = LocalDateTime.parse(dateString);

                if (dateImages.isBefore(revisionDate)) {
                    revisionImages.add(image);
                }
            }
        }
        return revisionImages;
    }

    private List<ImagesDTO> getImagesBetweenRevisions(List<ImagesDTO> images,
                                                      BaseRevisionEntity previousRevision,
                                                      BaseRevisionEntity currentRevision) {
        List<ImagesDTO> revisionImages = new ArrayList<>();

        LocalDateTime previousRevisionDate = Instant.ofEpochMilli(previousRevision.getRevtstmp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime currentRevisionDate = Instant.ofEpochMilli(currentRevision.getRevtstmp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        for (ImagesDTO image : images) {
            if (image.getName().contains("Фото")) {
                try {
                    String dateString = image.getName().substring(7).strip();
                    LocalDateTime dateImages = LocalDateTime.parse(dateString);

                    if (dateImages.isAfter(previousRevisionDate) && dateImages.isBefore(currentRevisionDate)) {
                        revisionImages.add(image);
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга даты из имени файла: " + image.getName());
                }
            }
        }
        return revisionImages;
    }

    private Set<String> getInconsistenciesForRevisionNative(UUID requestId, Long revisionNumber) {
        try {
            String sql = "SELECT i.name " +
                    "FROM rces_history.request_incosistencies_history ria " +
                    "JOIN rces.inconsistencies i ON ria.incosistency_id = i.id " +
                    "WHERE ria.request_id = :hexUuid " +
                    "AND ria.REV = :rev " +
                    "AND ria.REVTYPE != 2";

            @SuppressWarnings("unchecked")
            List<String> names = em.createNativeQuery(sql)
                    .setParameter("hexUuid", requestId)
                    .setParameter("rev", revisionNumber)
                    .getResultList();

            return new HashSet<>(names);

        } catch (Exception e) {
            return Collections.emptySet();
        }
    }
}
