package com.example.rces.models;

import com.example.rces.configuration.HashMapConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "sgi_log", catalog = "rces")
public class SgiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sgi_id")
    private SGI sgi;

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

    public SGI getSgi() {
        return sgi;
    }

    public void setSgi(SGI sgi) {
        this.sgi = sgi;
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

    public SgiLog(SGI sgi, Employee user, Map<String, String> metadata) {
        this.sgi = sgi;
        this.date = LocalDateTime.now();
        this.user = user;
        this.metadata = metadata;
    }

    public SgiLog() {
    }
}
