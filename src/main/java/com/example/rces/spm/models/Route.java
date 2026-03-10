package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.util.List;

@Entity
@Subselect("SELECT * FROM pdm_route")
@Immutable
public class Route {

    @Id
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    private Item item;

    @Column(name = "description")
    private String description;

    @Column(name="is_primary")
    private Boolean isPrimary; //Основной

    @Column(name="route_type")
    private String routeType; //Тип маршрута

    @OneToMany(mappedBy="route", fetch = FetchType.LAZY)
    private List<RouteRevision> revisions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public List<RouteRevision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<RouteRevision> revisions) {
        this.revisions = revisions;
    }
}
