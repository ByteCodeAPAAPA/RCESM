package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;

@Entity
@Subselect("SELECT * FROM pdm_component")
@Immutable
public class Component {

    @Id
    private Long id;

    @Column(name = "number")
    private Integer number; // Позиция

    @Column(name = "qty")
    private BigDecimal qty;

    @ManyToOne(fetch= FetchType.LAZY)
    private RouteRevision routerevision;

    @ManyToOne(fetch=FetchType.LAZY)
    private Step step; // Заход

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="component_route_id")
    private Route componentRoute; // Маршрут

    @Column(name="offset_time")
    private BigDecimal offsetTime; // Смещение

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="component_item_id")
    private Item componentItem;

    @ManyToOne(fetch=FetchType.LAZY)
    private UnitMeasure unitmeasure; // ЕИ

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public RouteRevision getRouterevision() {
        return routerevision;
    }

    public void setRouterevision(RouteRevision routerevision) {
        this.routerevision = routerevision;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Route getComponentRoute() {
        return componentRoute;
    }

    public void setComponentRoute(Route componentRoute) {
        this.componentRoute = componentRoute;
    }

    public BigDecimal getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(BigDecimal offsetTime) {
        this.offsetTime = offsetTime;
    }

    public Item getComponentItem() {
        return componentItem;
    }

    public void setComponentItem(Item componentItem) {
        this.componentItem = componentItem;
    }

    public UnitMeasure getUnitmeasure() {
        return unitmeasure;
    }

    public void setUnitmeasure(UnitMeasure unitmeasure) {
        this.unitmeasure = unitmeasure;
    }
}
