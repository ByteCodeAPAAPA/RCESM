package com.example.rces.dto;

import java.util.ArrayList;
import java.util.List;

public class MachineDto {

    private String name;
    private String description;
    private Integer number;
    private List<EmployeeDTO> admittedEmployeesList = new ArrayList<>();
    private List<EmployeeDTO> responsibleEmployeesList = new ArrayList<>();
    private List<ImagesDTO> imageUrls = new ArrayList<>();
    private List<DocumentFileDTO> pdfs = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<EmployeeDTO> getAdmittedEmployeesList() {
        return admittedEmployeesList;
    }

    public void setAdmittedEmployeesList(List<EmployeeDTO> admittedEmployeesList) {
        this.admittedEmployeesList = admittedEmployeesList;
    }

    public List<EmployeeDTO> getResponsibleEmployeesList() {
        return responsibleEmployeesList;
    }

    public void setResponsibleEmployeesList(List<EmployeeDTO> responsibleEmployeesList) {
        this.responsibleEmployeesList = responsibleEmployeesList;
    }

    public List<ImagesDTO> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<ImagesDTO> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<DocumentFileDTO> getPdfs() {
        return pdfs;
    }

    public void setPdfs(List<DocumentFileDTO> pdfs) {
        this.pdfs = pdfs;
    }
}
