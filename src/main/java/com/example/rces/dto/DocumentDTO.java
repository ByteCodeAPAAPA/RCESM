package com.example.rces.dto;

import java.util.List;
import java.util.UUID;

public class DocumentDTO {

    private UUID id;

    private String name;

    private List<DocumentFileDTO> files;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DocumentFileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<DocumentFileDTO> files) {
        this.files = files;
    }
}
