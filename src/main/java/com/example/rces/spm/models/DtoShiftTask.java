package com.example.rces.spm.models;

import java.math.BigDecimal;

public class DtoShiftTask {

    private Long id;
    private Long jobStepId;
    private Long mlmNodeId;
    private String customerOrder;
    private String description;
    private BigDecimal qtyPlan;
    private BigDecimal qtyFinished;
    private String dateStart;
    private String dateEnd;
    private String mlmNodeName;
    private String jobOrderName;
    private Double equipment;

    public Long getMlmNodeId() {
        return mlmNodeId;
    }

    public void setMlmNodeId(Long mlmNodeId) {
        this.mlmNodeId = mlmNodeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobStepId() {
        return jobStepId;
    }

    public void setJobStepId(Long jobStepId) {
        this.jobStepId = jobStepId;
    }

    public Double getEquipment() {
        return equipment;
    }

    public void setEquipment(Double equipment) {
        this.equipment = equipment;
    }

    public String getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(String customerOrder) {
        this.customerOrder = customerOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQtyPlan() {
        return qtyPlan;
    }

    public void setQtyPlan(BigDecimal qtyPlan) {
        this.qtyPlan = qtyPlan;
    }

    public BigDecimal getQtyFinished() {
        return qtyFinished;
    }

    public void setQtyFinished(BigDecimal qtyFinished) {
        this.qtyFinished = qtyFinished;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getMlmNodeName() {
        return mlmNodeName;
    }

    public void setMlmNodeName(String mlmNodeName) {
        this.mlmNodeName = mlmNodeName;
    }

    public String getJobOrderName() {
        return jobOrderName;
    }

    public void setJobOrderName(String jobOrderName) {
        this.jobOrderName = jobOrderName;
    }
}
