package com.example.rces.spm.services;

import com.example.rces.spm.services.service.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "spmTransactionManager")
public class SPMService {
    private final SPMRepository repository;

    private final PrimaryDemandService primaryDemandService;
    private final JobComponentService jobComponentService;
    private final ItemService itemService;
    private final CustomerOrderService customerOrderService;
    private final SPMReportService SPMReportService;


    @Autowired
    public SPMService(SPMRepository repository,
                      PrimaryDemandService primaryDemandService,
                      JobComponentService jobComponentService,
                      ItemService itemService,
                      CustomerOrderService customerOrderService,
                      SPMReportService SPMReportService) {
        this.repository = repository;
        this.primaryDemandService = primaryDemandService;
        this.jobComponentService = jobComponentService;
        this.itemService = itemService;
        this.customerOrderService = customerOrderService;
        this.SPMReportService = SPMReportService;
    }

    public <T> T findById(Class<T> entity, Object id) {
        return repository.findById(entity, id);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return repository.findAll(entityClass);
    }

    public <T> List<T> findAllByField(Class<T> entityClass, String fieldName, Object fieldValue) {
        return repository.findByField(entityClass, fieldName, fieldValue);
    }

    public <T> Page<T> getPage(Class<T> entityClass, int page, int pageSize, Sort sort, String conditions) {
        return repository.getPageByEntity(entityClass, page, pageSize, sort, conditions);
    }

    public EntityManager getEntityManager() {
        return repository.getEntityManager();
    }

    public <T> T findSingleByField(Class<T> entityClass, String fieldName, Object fieldValue) {
        return findAllByField(entityClass, fieldName, fieldValue).get(0);
    }

    public PrimaryDemandService getPrimaryDemandService() {
        return primaryDemandService;
    }

    public JobComponentService getJobComponentService() {
        return jobComponentService;
    }

    public ItemService getItemService() {
        return itemService;
    }

    public CustomerOrderService getCustomerOrderService() {
        return customerOrderService;
    }

    public SPMReportService getReportService() {
        return SPMReportService;
    }
}
