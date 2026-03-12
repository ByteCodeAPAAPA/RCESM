package com.example.rces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionViolationCreateDTO {

    private Integer inspectionId;

    private String description;

    private String criteria;

    private Integer score;

    private String subDivision;

    public Integer getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Integer inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(String subDivision) {
        this.subDivision = subDivision;
    }
}
