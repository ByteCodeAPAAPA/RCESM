package com.example.rces.repository;

import com.example.rces.models.RequestLog;
import com.example.rces.models.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestLogRepository extends JpaRepository<RequestLog, UUID> {

    List<RequestLog> findAllByRequest(Requests request);

    List<RequestLog> findAllByRequestId(UUID requestId);
}

