package com.example.rces.repository;

import com.example.rces.models.FactExecutionSGI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FactExecutionSgiRepository extends BaseAuditingRepository<FactExecutionSGI, UUID> {

}
