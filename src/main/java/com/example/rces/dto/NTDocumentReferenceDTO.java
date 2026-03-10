package com.example.rces.dto;

import com.example.rces.models.enums.Color;

import java.time.LocalDate;
import java.util.UUID;

public class NTDocumentReferenceDTO {

    private UUID id;

    private Long version;

    private String name;

    private String type;

    private LocalDate dateVerification;

    private UUID documentId;

    private String comment;

    private Color color;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public LocalDate getDateVerification() {
        return dateVerification;
    }

    public void setDateVerification(LocalDate dateVerification) {
        this.dateVerification = dateVerification;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
