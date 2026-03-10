package com.example.rces.repository;

import com.example.rces.models.SPE;
import jakarta.persistence.LockModeType;
import org.hibernate.annotations.Parameter;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpeRepository extends BaseAuditingRepository<SPE, Integer> {

    @Override
    @EntityGraph(attributePaths = {"employee", "employee.subDivision", "subDivision"})
    @NonNull
    List<SPE> findAll();

    @Lock(LockModeType.OPTIMISTIC )
    @Query("SELECT s FROM SPE s LEFT JOIN FETCH s.document WHERE s.number = :number")
    Optional<SPE> findByIdWithDocument(@Param("number") Integer number);

}
