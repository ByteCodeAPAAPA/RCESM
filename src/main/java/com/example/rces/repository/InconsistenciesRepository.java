package com.example.rces.repository;

import com.example.rces.dto.InconsistencyDto;
import com.example.rces.models.Inconsistency;

import java.util.List;


public interface InconsistenciesRepository extends BaseAuditingRepository<Inconsistency, Long> {

    List<Inconsistency> findAllByControlType(String type);
}
