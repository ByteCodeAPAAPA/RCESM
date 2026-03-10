package com.example.rces.service;

import com.example.rces.dto.CustomerOrderDTO;
import com.example.rces.models.CustomerOrder;
import com.example.rces.models.Employee;

import java.util.List;

public interface CustomerOrderService {

    CustomerOrder createOrGetCustomerOrder(Employee createdEmployee, String customerOrderName, String customerOrderJson);

    List<CustomerOrder> findAll();

    List<CustomerOrderDTO> findAllPayload();
}
