package com.example.rces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InspectionCreateDTO {

    private String subDivision;

    private String type;

    private Integer primaryInspectionId;

    public String getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(String subDivision) {
        this.subDivision = subDivision;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPrimaryInspectionId() {
        return primaryInspectionId;
    }

    public void setPrimaryInspectionId(Integer primaryInspectionId) {
        this.primaryInspectionId = primaryInspectionId;
    }
}
