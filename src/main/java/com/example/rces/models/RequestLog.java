package com.example.rces.models;

import com.example.rces.configuration.HashMapConverter;
import jakarta.persistence.*;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "request_log", catalog = "rces")
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Requests request;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee user;

    @Column(columnDefinition = "JSON")
    @Convert(converter = HashMapConverter.class)
    private Map<String, String> metadata = new LinkedHashMap<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Requests getRequest() {
        return request;
    }

    public void setRequest(Requests request) {
        this.request = request;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Employee getUser() {
        return user;
    }

    public void setUser(Employee user) {
        this.user = user;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }


    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Deprecated(forRemoval = true)
    public void addToMetadata(Map<String, String> newMetadata) {
        newMetadata.forEach((key, newValue) -> {
            if (metadata.containsKey(key)) {
                String existingValue = metadata.get(key);
                if (!newValue.equals(existingValue)) {
                    int version = 2;
                    while (metadata.containsKey(key + " v." + version)) {
                        version++;
                    }
                    metadata.put(key + " v." + version, newValue);
                }
            } else {
                metadata.put(key, newValue);
            }
        });
    }

    public RequestLog(Requests request, Employee user, Map<String, String> metadata) {
        this.request = request;
        this.date = LocalDateTime.now();
        this.user = user;
        this.metadata = metadata;
    }

    public RequestLog() {
    }
}