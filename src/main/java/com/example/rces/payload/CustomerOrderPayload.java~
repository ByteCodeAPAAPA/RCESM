package com.example.rces.controller.payload;

import com.example.rces.models.CustomerOrder;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerOrderPayload(UUID id, String name, LocalDateTime createDate, String employeeName) {

    public CustomerOrderPayload(CustomerOrder customerOrder) {
        this(
        customerOrder.getId(),
        customerOrder.getName(),
        LocalDateTime.from(customerOrder.getCreatedDate()),
        customerOrder.getCreatedBy() != null ? customerOrder.getCreatedBy().getName() : null
        );
    }
}
