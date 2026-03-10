package com.example.rces.service.impl;

import com.example.rces.dto.CustomerOrderDTO;
import com.example.rces.mapper.CustomerOrderMapper;
import com.example.rces.models.CustomerOrder;
import com.example.rces.models.Employee;
import com.example.rces.repository.CustomerOrderRepository;
import com.example.rces.service.CustomerOrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "primaryTransactionManager")
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final CustomerOrderRepository repository;
    private final ObjectMapper objectMapper;
    private final CustomerOrderMapper mapper;

    @Autowired
    public CustomerOrderServiceImpl(CustomerOrderRepository repository, ObjectMapper objectMapper, CustomerOrderMapper mapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
    }

    @Override
    public List<CustomerOrderDTO> findAllPayload() {
        List<CustomerOrder> orders = repository.findAll();
        return orders.stream().map(mapper::toDTO).toList();
    }

    @Override
    public CustomerOrder createOrGetCustomerOrder(Employee createdEmployee, String customerOrderName, String customerOrderJson) {
        try {
            String orderNameToUse = customerOrderName;

            if (customerOrderJson != null && !customerOrderJson.isBlank()) {
                JsonNode jsonNode = objectMapper.readTree(customerOrderJson);
                if (jsonNode.has("name") && jsonNode.get("name").isTextual()) {
                    orderNameToUse = jsonNode.get("name").asText();
                }
            }

            if (orderNameToUse == null || orderNameToUse.isBlank()) {
                return null;
            }

            CustomerOrder existingOrder = repository.findByName(orderNameToUse);
            if (existingOrder != null) {
                return existingOrder;
            }

            CustomerOrder newOrder = new CustomerOrder();
            newOrder.setName(orderNameToUse);
            return repository.save(newOrder);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при десериализации JSON CustomerOrder", e);
        }
    }

    @Override
    public List<CustomerOrder> findAll() {
        return repository.findAll();
    }

}
