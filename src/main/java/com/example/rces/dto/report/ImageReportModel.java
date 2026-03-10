package com.example.rces.dto.report;

import java.io.InputStream;
import java.time.LocalDateTime;

public class ImageReportModel {

    private String name;

    private InputStream image;

    public ImageReportModel(String name, InputStream image) {
        this.name = name!=null ? name : LocalDateTime.now().toString();
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public InputStream getImage() {
        return image;
    }

    public void setImage(InputStream image) {
        this.image = image;
    }
}
