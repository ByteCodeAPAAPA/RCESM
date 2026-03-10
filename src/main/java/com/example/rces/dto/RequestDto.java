package com.example.rces.dto;

public class RequestDto {

    private String type;
    private String employeeJson;
    private String mlmNodeJson;
    private String titleJson;
    private String itemNameJson;
    private Integer qty;
    private String control;
    private String customerOrderString;
    private String customerOrderJson;
    private String reasonsJson;
    private String comment;
    private int requestNumber;

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmployeeJson() {
        return employeeJson;
    }

    public void setEmployeeJson(String employeeJson) {
        this.employeeJson = employeeJson;
    }

    public String getMlmNodeJson() {
        return mlmNodeJson;
    }

    public void setMlmNodeJson(String mlmNodeJson) {
        this.mlmNodeJson = mlmNodeJson;
    }

    public String getTitleJson() {
        return titleJson;
    }

    public void setTitleJson(String titleJson) {
        this.titleJson = titleJson;
    }

    public String getItemNameJson() {
        return itemNameJson;
    }

    public void setItemNameJson(String itemNameJson) {
        this.itemNameJson = itemNameJson;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getCustomerOrderString() {
        return customerOrderString;
    }

    public void setCustomerOrderString(String customerOrderString) {
        this.customerOrderString = customerOrderString;
    }

    public String getCustomerOrderJson() {
        return customerOrderJson;
    }

    public void setCustomerOrderJson(String customerOrderJson) {
        this.customerOrderJson = customerOrderJson;
    }

    public String getReasonsJson() {
        return reasonsJson;
    }

    public void setReasonsJson(String reasonsJson) {
        this.reasonsJson = reasonsJson;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
