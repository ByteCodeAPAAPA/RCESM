package com.example.rces.service.impl;

import com.example.rces.models.Requests;
import com.example.rces.models.SGI;
import com.example.rces.repository.RequestsRepository;
import com.example.rces.repository.SgiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class WebSecurityService {

    private final RequestsRepository requestsRepository;
    private final SgiRepository sgiRepository;

    @Autowired
    public WebSecurityService(RequestsRepository requestsRepository, SgiRepository sgiRepository) {
        this.requestsRepository = requestsRepository;
        this.sgiRepository = sgiRepository;
    }

    public Requests findByRequestNumber(Integer requestNumber) {
        return requestsRepository.findByRequestNumber(requestNumber);
    }

    public void save(Requests requests) {
        requestsRepository.save(requests);
    }

    public List<SGI> findAll() {
        return sgiRepository.findAll();
    }
}
