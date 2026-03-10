package com.example.rces.repository;

import com.example.rces.dto.InspectionViolationDTO;
import com.example.rces.models.Inspection;
import com.example.rces.models.InspectionViolation;
import jakarta.persistence.NamedQuery;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InspectionViolationRepository extends BaseAuditingRepository<InspectionViolation, UUID> {

    @Query("""
            SELECT i FROM InspectionViolation i
            LEFT JOIN FETCH i.subDivision
            LEFT JOIN FETCH i.createdBy cb
            LEFT JOIN FETCH cb.subDivision
            LEFT JOIN FETCH i.inspection
            WHERE i.inspection.id = :id
            ORDER BY i.createdDate
    """)
    List<InspectionViolation> findAllByInspectionId(@Param("id") Integer id);

    @Query("SELECT i " +
            "FROM InspectionViolation i " +
            "LEFT JOIN FETCH i.subDivision s " +
            "WHERE s.code = :subDivision " +
            "AND i.createdDate >= :startDate " +
            "AND i.createdDate < :endDate " +
            "AND i.status = 'status1'")
    List<InspectionViolation> notFixedInspectionViolation(
            @Param("subDivision") String subDivision,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    @Query( name = "InspectionViolation.findAllNotFixed")
    List<InspectionViolation> findAllInspectionViolationServices();

}
