package com.example.rces.dto;

import com.example.rces.models.enums.Format;

import java.util.UUID;

public class DocumentFileDTO {

    private UUID id;

    private String baseFileName;

    private Format type;

    public DocumentFileDTO() {
    }

    public DocumentFileDTO(UUID id, String baseFileName, Format type) {
        this.id = id;
        this.baseFileName = baseFileName;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBaseFileName() {
        return baseFileName;
    }

    public void setBaseFileName(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    public Format getType() {
        return type;
    }

    public void setType(Format type) {
        this.type = type;
    }
}
