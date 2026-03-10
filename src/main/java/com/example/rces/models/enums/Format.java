package com.example.rces.models.enums;

import org.springframework.http.MediaType;

public enum Format {
    PDF(MediaType.APPLICATION_PDF, "pdf"),
    XLS("application/vnd.ms-excel", "xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    DOC("application/msword", "doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    RTF("application/rtf", "rtf"),
    ODT("application/vnd.oasis.opendocument.text", "odt"),
    JSON(MediaType.APPLICATION_JSON, "json"),
    TXT(MediaType.TEXT_PLAIN, "txt"),
    HTML(MediaType.TEXT_HTML, "html"),
    XML(MediaType.APPLICATION_XML, "xml");

    private final String mimeType;
    private final String fileExtension;

    Format(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    Format(MediaType mimeType, String fileExtension) {
        this.mimeType = mimeType.toString();
        this.fileExtension = fileExtension;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }
}
