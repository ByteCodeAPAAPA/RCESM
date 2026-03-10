package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.util.List;

@Entity
@Subselect("SELECT * FROM pdm_routerevision")
@Immutable
public class RouteRevision {

    @Id
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    private Route route;

    @Column(name = "name")
    private String name; //Ревизия

    @Column(name = "description")
    private String description; //Описание

    @ManyToOne(fetch=FetchType.LAZY)
    private Status status;

    @OneToMany(mappedBy="routeRevision", fetch = FetchType.LAZY)
    @OrderBy("routeRevision, number")
    private List<Step> steps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
