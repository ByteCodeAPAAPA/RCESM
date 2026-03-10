package com.example.rces.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {

    SUCCESS("success"),
    ERROR("error"),
    WARNING("warning"),
    INFO("info");

    NotificationType(String name) {
        this.name = name;
    }

    private final String name;

    @JsonValue
    public String getName() {
        return name;
    }
}
