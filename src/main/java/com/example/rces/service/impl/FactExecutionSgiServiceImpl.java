package com.example.rces.service.impl;

import com.example.rces.models.FactExecutionSGI;
import com.example.rces.models.SGI;
import com.example.rces.repository.FactExecutionSgiRepository;
import com.example.rces.service.FactExecutionSgiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class FactExecutionSgiServiceImpl implements FactExecutionSgiService {

    private final FactExecutionSgiRepository repository;

    @Autowired
    public FactExecutionSgiServiceImpl(FactExecutionSgiRepository repository) {
        this.repository = repository;
    }

    @Override
    public FactExecutionSGI createFactExecutionSGI(SGI sgi) {
        FactExecutionSGI factExecutionSGI = new FactExecutionSGI();
        factExecutionSGI.setSgi(sgi);
        return factExecutionSGI;
    }
}
