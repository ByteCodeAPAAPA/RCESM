package com.example.rces.payload;

import com.example.rces.models.FactExecutionSGI;

import java.util.List;
import java.util.UUID;

public record FactExecutionPayload(UUID id, String executionDate, String report, List<ImagesPayload> imagesFactSGI) {

    public FactExecutionPayload(FactExecutionSGI factExecutionSGI) {
        this(
                factExecutionSGI.getId(),
                factExecutionSGI.getExecutionDate()!=null ? factExecutionSGI.getExecutionDate().toString() : null,
                factExecutionSGI.getReport(),
                null
        );
    }
}
