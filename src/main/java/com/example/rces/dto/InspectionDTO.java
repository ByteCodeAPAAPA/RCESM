package com.example.rces.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class InspectionDTO {

    private Integer id;

    private SubDivisionDTO subDivision;

    private LocalDateTime dateInspection;

    private String type;

    private List<InspectionViolationDTO> violation;

    private Boolean haveSecondInspection;

    private Integer primaryInspectionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SubDivisionDTO getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivisionDTO subDivision) {
        this.subDivision = subDivision;
    }

    public LocalDateTime getDateInspection() {
        return dateInspection;
    }

    public void setDateInspection(LocalDateTime dateInspection) {
        this.dateInspection = dateInspection;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<InspectionViolationDTO> getViolation() {
        return violation;
    }

    public void setViolation(List<InspectionViolationDTO> violation) {
        this.violation = violation;
    }

    public Boolean getHaveSecondInspection() {
        return haveSecondInspection;
    }

    public void setHaveSecondInspection(Boolean haveSecondInspection) {
        this.haveSecondInspection = haveSecondInspection;
    }

    public Integer getPrimaryInspectionId() {
        return primaryInspectionId;
    }

    public void setPrimaryInspectionId(Integer primaryInspectionId) {
        this.primaryInspectionId = primaryInspectionId;
    }

}
