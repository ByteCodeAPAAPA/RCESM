package com.example.rces.dto;

import java.util.UUID;

public class ImagesDTO {

    private UUID id;

    private String name;

    private String data;

    private UUID mainlink;


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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public UUID getMainlink() {
        return mainlink;
    }

    public void setMainlink(UUID mainlink) {
        this.mainlink = mainlink;
    }
}
