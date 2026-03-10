package com.example.rces.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class SgiCreateDTO {

    private String workcenter;
    private String event;
    private String actions;
    private String department;
    private EmployeeDTO employee;
    private LocalDate desiredDate;
    private String note;
    private String parentId;

    public SgiCreateDTO() {
    }

    public SgiCreateDTO(String workcenter, String event, String actions, String department, EmployeeDTO employee,
                        LocalDate desiredDate, String note, String parentId) {
        this.workcenter = workcenter;
        this.event = event;
        this.actions = actions;
        this.department = department;
        this.employee = employee;
        this.desiredDate = desiredDate;
        this.note = note;
        this.parentId = parentId;
    }

    public String getWorkcenter() {
        return workcenter;
    }

    public void setWorkcenter(String workcenter) {
        this.workcenter = workcenter;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public LocalDate getDesiredDate() {
        return desiredDate;
    }

    public void setDesiredDate(LocalDate desiredDate) {
        this.desiredDate = desiredDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
