package com.example.rces.repository;

import com.example.rces.models.CustomerOrder;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerOrderRepository extends BaseAuditingRepository<CustomerOrder, UUID> {

    CustomerOrder findByName(String customerOrderName);

}
