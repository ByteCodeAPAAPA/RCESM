package com.example.rces.dto;

import java.util.UUID;

public class CreateByRequest {

    private UUID requestId;
    private String user;

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
