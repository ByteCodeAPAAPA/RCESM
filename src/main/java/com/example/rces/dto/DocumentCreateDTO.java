package com.example.rces.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class DocumentCreateDTO {

    private String name;

    private List<MultipartFile> files;

    private MultipartFile[] images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    public MultipartFile[] getImages() {
        return images;
    }

    public void setImages(MultipartFile[] images) {
        this.images = images;
    }
}
