package com.example.rces.utils;

import com.example.rces.dto.DocumentCreateDTO;
import com.example.rces.models.*;
import com.example.rces.models.enums.Format;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilesUtil {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "PDF", "DOC", "XLS", "XLSX", "DOCX", "XML", "TXT", "JSON"
    );

    public static List<Images> addImages(MultipartFile[] files, Requests requests) {
        List<Images> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Images imageEntity = new Images();
                imageEntity.setName(file.getOriginalFilename());
                try {
                    imageEntity.setData(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                imageEntity.setRequest(requests);
                images.add(imageEntity);
            }
        }
        return images;
    }

    public static List<Images> addImages(MultipartFile[] files, FactExecutionSGI sgi) {
        List<Images> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Images imageEntity = new Images();
                imageEntity.setName(file.getOriginalFilename());
                try {
                    imageEntity.setData(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                imageEntity.setSgi(sgi);
                images.add(imageEntity);
            }
        }
        return images;
    }

    public static List<Images> addImages(MultipartFile[] files, SGI sgi) {
        List<Images> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Images imageEntity = new Images();
                imageEntity.setName(file.getOriginalFilename());
                try {
                    imageEntity.setData(file.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                imageEntity.setSgim(sgi);
                images.add(imageEntity);
            }
        }
        return images;
    }

    public static List<Images> addImages(MultipartFile[] files, Document document) {
        List<Images> images = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Images imageEntity = new Images();
                    imageEntity.setName(file.getOriginalFilename());
                    try {
                        imageEntity.setData(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    imageEntity.setDocument(document);
                    images.add(imageEntity);
                }
            }
        }
        return images;
    }

    //Изменение коллекции изображений
    public static void handleImageCollection(Requests request, List<?> newImages) {
        List<Images> currentImages = request.getImages();
        List<Images> toRemove = new ArrayList<>(currentImages);
        if (newImages != null) {
            toRemove.removeIf(newImages::contains);
        }
        toRemove.forEach(img -> img.setRequest(null));
        currentImages.removeAll(toRemove);
        if (newImages != null) {
            for (Object img : newImages) {
                Images image = (Images) img;
                if (!currentImages.contains(image)) {
                    image.setRequest(request);
                    currentImages.add(image);
                }
            }
        }
    }

    public static boolean isJson(Object value) {
        if (value == null) {
            return false;
        }
        String str;
        try {
            str = value.toString();
        } catch (Exception e) {
            return false;
        }
        str = str.trim();
        if (!str.startsWith("{") && !str.startsWith("[")) {
            return false;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(str);
            return node.isObject() || node.isArray();
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new ApplicationContextException("Ошибка при получении массива байтов: " + file.getOriginalFilename(), e);
        }
    }

    public static List<DocumentFile> addFilesToDocument(Document document, List<MultipartFile> files) {
        List<DocumentFile> documentFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            DocumentFile documentFile = new DocumentFile();

            Format fileType = determineFileType(file.getOriginalFilename());

            documentFile.setBaseFileName(file.getOriginalFilename());
            documentFile.setType(fileType);
            documentFile.setDocument(document);
            documentFile.setContent(getBytes(file));

            documentFiles.add(documentFile);
        }
        return documentFiles;
    }

    public static DocumentFile addFileToDocument(Document document, byte[] content) {
        DocumentFile documentFile = new DocumentFile();

        documentFile.setBaseFileName(document.getName() + ".pdf");
        documentFile.setType(Format.PDF);
        documentFile.setDocument(document);
        documentFile.setContent(content);

        return documentFile;
    }

    public static DocumentFile addFileToDocument(Document document, MultipartFile file) {
        DocumentFile documentFile = new DocumentFile();

        documentFile.setBaseFileName(file.getOriginalFilename());
        documentFile.setType(determineFileType(file.getOriginalFilename()));
        documentFile.setDocument(document);
        documentFile.setContent(getBytes(file));

        return documentFile;
    }

    public static void validateDocument(DocumentCreateDTO documentDTO) {
        if (documentDTO.getName() == null || documentDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Наименование документа не может быть пустым");
        }
        if (documentDTO.getFiles() != null) {
            for (MultipartFile file : documentDTO.getFiles()) {
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("Файл не может быть пустым");
                }
                if (file.getOriginalFilename() != null && !isValidFileType(file.getOriginalFilename())) {
                    throw new IllegalArgumentException("Недопустимое расширение файла");
                }
            }
        }
    }


    public static boolean isValidFileType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public static Format determineFileType(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "pdf" -> Format.PDF;
            case "doc" -> Format.DOC;
            case "docx" -> Format.DOCX;
            case "xls" -> Format.XLS;
            case "xlsx" -> Format.XLSX;
            case "xml" -> Format.XML;
            case "txt" -> Format.TXT;
            case "json" -> Format.JSON;
            default -> null;
        };
    }

}