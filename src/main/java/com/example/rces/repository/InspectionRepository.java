package com.example.rces.repository;

import com.example.rces.models.Inspection;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends BaseAuditingRepository<Inspection, Integer> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"subDivision", "createdBy", "primaryInspection", "violation"})
    Optional<Inspection> findById(@Nullable Integer integer);

    @NotNull
    @Override
    @Query("SELECT DISTINCT i FROM Inspection i " +
            "LEFT JOIN FETCH i.subDivision sd " +
            "LEFT JOIN FETCH i.primaryInspection " +
            "ORDER BY sd.name ASC, i.type ASC")
    List<Inspection> findAll();

    @Query("SELECT COUNT(i) > 0 FROM Inspection i LEFT JOIN i.subDivision s WHERE s.code = :subDivision AND YEAR(i.createdDate) = YEAR(CURRENT_DATE) AND MONTH(i.createdDate) = MONTH(CURRENT_DATE)")
    boolean existsThisMonth(@Param("subDivision") String subDivision);

}
