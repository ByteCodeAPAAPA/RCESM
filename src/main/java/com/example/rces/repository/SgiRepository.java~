package com.example.rces.repository;

import com.example.rces.models.SGI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SgiRepository extends BaseAuditingRepository<SGI, UUID> {

    @Query(value = "SELECT COALESCE(MAX(requestNumber) + 1, 1) FROM SGI")
    int findNextRequestNumber();

    @EntityGraph(value = "SGI.withAssociations", type = EntityGraph.EntityGraphType.LOAD)
    @Query(value = "SELECT s FROM SGI s WHERE s.parentSGI is null ORDER BY s.requestNumber ASC")
    Page<SGI> findAllWithAssociations(Pageable pageable);

}
