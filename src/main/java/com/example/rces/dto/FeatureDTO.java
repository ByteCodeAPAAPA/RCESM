package com.example.rces.dto;

public class FeatureDTO {
    private String name;
    private String description;
    private boolean enabled;
    private String category;

    public FeatureDTO(String name, String description, boolean enabled, String category) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
