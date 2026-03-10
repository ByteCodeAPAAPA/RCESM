package com.example.rces.repository;

import com.example.rces.models.NTDocument;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StateStandardDocumentRepository extends BaseAuditingRepository<NTDocument, UUID> {

    @Override
    @EntityGraph(attributePaths = {"references"})
    @NonNull
    List<NTDocument> findAll();

    @Query("SELECT e FROM NTDocument e WHERE e.id NOT IN :ids")
    List<NTDocument> findAllWithoutIds(List<UUID> ids);

}
