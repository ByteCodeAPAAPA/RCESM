package com.example.rces.dto;

import com.example.rces.models.Inconsistency;
import com.example.rces.models.Requests;
import com.example.rces.models.enums.GeneralReason;
import com.example.rces.models.enums.Item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RequestHistoryDTO {

    private UUID requestId;
    private RequestHistoryData requestData;
    private Long revisionNumber;
    private LocalDateTime revisionDate;
    private String revisionType;
    private String changedBy;


    public static class RequestHistoryData {
        private Requests.Type typeRequest; //Оставлять
        private Integer requestNumber; //Оставлять
        private Integer qty; //Оставлять
        private String status; //Оставлять
        private String description; //Оставлять
        private Set<String> inconsistencies;
        private List<ImagesDTO> images = new ArrayList<>();

        public RequestHistoryData() {
        }

        public List<ImagesDTO> getImages() {
            return images;
        }

        public void setImages(List<ImagesDTO> images) {
            this.images = images;
        }

        public Set<String> getInconsistencies() {
            return inconsistencies;
        }

        public void setInconsistencies(Set<String> inconsistencies) {
            this.inconsistencies = inconsistencies;
        }

        public Requests.Type getTypeRequest() {
            return typeRequest;
        }

        public void setTypeRequest(Requests.Type typeRequest) {
            this.typeRequest = typeRequest;
        }

        public Integer getRequestNumber() {
            return requestNumber;
        }

        public void setRequestNumber(Integer requestNumber) {
            this.requestNumber = requestNumber;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static RequestHistoryData from(Requests request) {
            RequestHistoryData data = new RequestHistoryData();
            data.setTypeRequest(request.getTypeRequest());
            data.setRequestNumber(request.getRequestNumber());
            data.setQty(request.getQty());
            data.setStatus(request.getStatus().getName());
            data.setDescription(request.getDescription());
            if (request.getInconsistencies() != null) {
                Set<String> inconsistencyNames = request.getInconsistencies().stream()
                        .map(Inconsistency::getName)
                        .collect(Collectors.toSet());
                data.setInconsistencies(inconsistencyNames);
            }
            return data;
        }
    }

    // Геттеры и сеттеры для основного класса
    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public RequestHistoryData getRequestData() {
        return requestData;
    }

    public void setRequestData(RequestHistoryData requestData) {
        this.requestData = requestData;
    }

    public Long getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(Long revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public LocalDateTime getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(LocalDateTime revisionDate) {
        this.revisionDate = revisionDate;
    }

    public String getRevisionType() {
        return revisionType;
    }

    public void setRevisionType(String revisionType) {
        this.revisionType = revisionType;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String changeRequestDate() {
        if (revisionDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return revisionDate.format(formatter);
        }
        return "";
    }

}