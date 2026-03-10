package com.example.rces.repository;

import com.example.rces.models.SubDivision;
import org.springframework.stereotype.Repository;

@Repository
public interface SubDivisionRepository extends BaseAuditingRepository<SubDivision, Long> {

    SubDivision findByName(String name);

    SubDivision findByCode(String code);

}
