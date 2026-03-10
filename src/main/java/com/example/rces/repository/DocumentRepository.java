package com.example.rces.repository;

import com.example.rces.models.Document;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends BaseAuditingRepository<Document, UUID> {

}
