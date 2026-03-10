package com.example.rces.models;

import jakarta.persistence.*;

@Entity
@Table(name = "reason")
public class Reason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @Column(name = "type_request")
    @Enumerated(EnumType.STRING)
    private Requests.Type type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Requests.Type getType() {
        return type;
    }

    public void setType(Requests.Type type) {
        this.type = type;
    }
}
