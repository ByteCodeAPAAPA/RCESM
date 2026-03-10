package com.example.rces.models;

import jakarta.persistence.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name = "images", catalog = "rces")
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Lob
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Requests request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sgi_id")
    private FactExecutionSGI sgi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sgim_id")
    private SGI sgim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insVio_id")
    private InspectionViolation insVio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    public Images() {
    }

    public Images(byte[] data, Requests request, FactExecutionSGI sgi, SGI sgim, Document document, InspectionViolation insVio, String name) {
        this.data = data;
        this.request = request;
        this.sgi = sgi;
        this.sgim = sgim;
        this.document = document;
        this.insVio = insVio;
        this.name = name;
    }

    public Images(String base64, Requests request) {
        setBase64Data(base64);
        setName("Фото от " + LocalDateTime.now());
        setRequest(request);
    }

    public Images(String base64, FactExecutionSGI sgi) {
        setBase64Data(base64);
        setName("Фото от " + LocalDateTime.now());
        setSgi(sgi);
    }

    public String getBase64Data() {
        return data != null ? "data:image/png;base64," + Base64.getEncoder().encodeToString(data) : "";
    }

    public ByteArrayInputStream getInputStreamData() {
        return data != null ? new ByteArrayInputStream(data) : null;
    }

    public void setBase64Data(String base64Image) {
        if (base64Image == null || !base64Image.startsWith("data:")) {
            throw new IllegalArgumentException("Некорректный формат изображения");
        }

        int commaIndex = base64Image.indexOf(',');
        if (commaIndex == -1) {
            throw new IllegalArgumentException("Некорректный формат изображения");
        }

        String metadata = base64Image.substring(5, commaIndex);
        String base64Data = base64Image.substring(commaIndex + 1);

        setData(Base64.getDecoder().decode(base64Data));
    }

    public UUID getMainlinkId() {
        if (request != null) {
            return request.getId();
        } else if (sgi != null) {
            return sgi.getId();
        } else if (sgim != null) {
            return sgim.getId();
        } else if (document != null) {
            return document.getId();
        } else if (insVio!=null) {
            return insVio.getId();
        }
        return null;
    }

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Requests getRequest() {
        return request;
    }

    public void setRequest(Requests request) {
        this.request = request;
    }

    public FactExecutionSGI getSgi() {
        return sgi;
    }

    public void setSgi(FactExecutionSGI sgi) {
        this.sgi = sgi;
    }

    public SGI getSgim() {
        return sgim;
    }

    public void setSgim(SGI sgim) {
        this.sgim = sgim;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public InspectionViolation getInsVio() {
        return insVio;
    }

    public void setInsVio(InspectionViolation insVio) {
        this.insVio = insVio;
    }
}
