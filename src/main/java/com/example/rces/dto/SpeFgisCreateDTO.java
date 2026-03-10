package com.example.rces.dto;

public class SpeFgisCreateDTO {

    private String outNumber;

    private String accuracyClass;

    private String limitMeasurement;

    private EmployeeDTO employee;

    private SubDivisionDTO subDivision;

    public String getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(String outNumber) {
        this.outNumber = outNumber;
    }

    public String getAccuracyClass() {
        return accuracyClass;
    }

    public void setAccuracyClass(String accuracyClass) {
        this.accuracyClass = accuracyClass;
    }

    public String getLimitMeasurement() {
        return limitMeasurement;
    }

    public void setLimitMeasurement(String limitMeasurement) {
        this.limitMeasurement = limitMeasurement;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public SubDivisionDTO getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivisionDTO subDivision) {
        this.subDivision = subDivision;
    }
}
