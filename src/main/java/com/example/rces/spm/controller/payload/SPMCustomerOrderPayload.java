package com.example.rces.spm.controller.payload;

import com.example.rces.spm.models.SPMCustomerOrder;

import java.time.LocalDate;

public record SPMCustomerOrderPayload(Long id, String name, LocalDate planDate, LocalDate contractDate, String site) {
    public SPMCustomerOrderPayload(SPMCustomerOrder co) {
        this(   co.getId(),
                co.getStrCode(),
                co.getDateDue()!=null ? co.getDateDue().toLocalDate() : null,
                co.getDateContract()!=null ? co.getDateContract().toLocalDate() : null,
                co.getSite().getName()
        );
    }
}
