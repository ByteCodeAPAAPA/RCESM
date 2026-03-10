package com.example.rces.service.impl;

import com.example.rces.dto.SubDivisionDTO;
import com.example.rces.mapper.SubDivisionMapper;
import com.example.rces.models.SubDivision;
import com.example.rces.repository.SubDivisionRepository;
import com.example.rces.service.SubDivisionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class SubDivisionServiceImpl implements SubDivisionService {

    private final SubDivisionRepository repository;
    private final SubDivisionMapper mapper;
    private final Map<String, SubDivision> cache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        var allSubDivisionList = repository.findAll();
        allSubDivisionList.forEach(subDivision -> cache.put(subDivision.getName(), subDivision));
    }

    @Autowired
    public SubDivisionServiceImpl(SubDivisionRepository repository, SubDivisionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<SubDivisionDTO> getAll() {
        return cache.values().stream().map(mapper::toDTO).toList();
    }

    @Override
    public SubDivision getByName(String name) {
        return cache.computeIfAbsent(name, repository::findByName);
    }

    @Override
    public SubDivision getByCode(String code) {
        return cache.computeIfAbsent(code, repository::findByCode);
    }

    @Override
    public SubDivisionDTO getDTOByName(String name) {
        var subDivision = cache.computeIfAbsent(name, repository::findByName);
        return mapper.toDTO(subDivision);
    }


}
