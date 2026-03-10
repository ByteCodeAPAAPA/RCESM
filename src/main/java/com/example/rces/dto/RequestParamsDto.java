package com.example.rces.dto;

import java.util.UUID;

public class RequestParamsDto {

    private String status;
    private UUID requestId;
    private String description;
    private Integer qtyCompleted;
    private String inconsistencyData;
    private String descriptionsCompleted;

    public String getDescriptionsCompleted() {
        return descriptionsCompleted;
    }

    public void setDescriptionsCompleted(String descriptionsCompleted) {
        this.descriptionsCompleted = descriptionsCompleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public Integer getQtyCompleted() {
        return qtyCompleted;
    }

    public void setQtyCompleted(Integer qtyCompleted) {
        this.qtyCompleted = qtyCompleted;
    }

    public String getInconsistencyData() {
        return inconsistencyData;
    }

    public void setInconsistencyData(String inconsistencyData) {
        this.inconsistencyData = inconsistencyData;
    }
}
