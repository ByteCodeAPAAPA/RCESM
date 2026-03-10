package com.example.rces.dto;

import com.example.rces.models.enums.Color;
import com.example.rces.models.enums.OrganizationSPE;

import java.time.LocalDate;
import java.util.UUID;

public class SpeDTO {

    private Integer id;
    private Long version;
    private String name;
    private String type;
    private String outNumber;
    private String accuracyClass;
    private SubDivisionDTO subDivision;
    private String limitMeasurement;
    private EmployeeDTO employee;
    private String mark;
    private LocalDate datePreparation;
    private LocalDate dateVerification;
    private String certificateNumber;
    private Integer periodicity;
    private UUID documentId;
    private String status;
    private Color color;
    private OrganizationSPE organization;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public SubDivisionDTO getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivisionDTO subDivision) {
        this.subDivision = subDivision;
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

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public LocalDate getDatePreparation() {
        return datePreparation;
    }

    public void setDatePreparation(LocalDate datePreparation) {
        this.datePreparation = datePreparation;
    }

    public LocalDate getDateVerification() {
        return dateVerification;
    }

    public void setDateVerification(LocalDate dateVerification) {
        this.dateVerification = dateVerification;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public Integer getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Integer periodicity) {
        this.periodicity = periodicity;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public OrganizationSPE getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationSPE organization) {
        this.organization = organization;
    }
}
