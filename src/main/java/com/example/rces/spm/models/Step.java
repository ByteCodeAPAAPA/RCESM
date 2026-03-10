package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;

@Entity
@Subselect("SELECT * FROM pdm_step")
@Immutable
public class Step {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routerevision_id")
    private RouteRevision routeRevision;

    @Column(name = "number")
    private Integer number; // № захода

    @Column(name = "description")
    private String description; // Описание

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mlm_node_id")
    private MlmNode mlmNode; // Узел ПЛМ

    @Column(name="buffer_in")
    private BigDecimal bufferIn; // Буфер до

    @Column(name="setup_time")
    private BigDecimal setupTime; // Время до

    @Column(name="queue_time")
    private BigDecimal queueTime; // Время очереди

    @Column(name="production_time")
    private BigDecimal productionTime; // Время производства

    @Column(name = "is_production_time_fixed")
    private boolean isProductionTimeFixed; // Фиксированное время

    @Column(name="resource_time")
    private BigDecimal resourceTime = BigDecimal.ZERO; // Трудоемкость (шт)

    @Column(name="finish_time")
    private BigDecimal finishTime; // Время после

    @Column(name="buffer_out")
    private BigDecimal bufferOut; // Буфер до

    @Column(name="offset_time")
    private BigDecimal offsetTime; // Смещение

    @Column(name="move_time")
    private BigDecimal moveTime; // Время перемещения

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RouteRevision getRouteRevision() {
        return routeRevision;
    }

    public void setRouteRevision(RouteRevision routeRevision) {
        this.routeRevision = routeRevision;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MlmNode getMlmNode() {
        return mlmNode;
    }

    public void setMlmNode(MlmNode mlmNode) {
        this.mlmNode = mlmNode;
    }

    public BigDecimal getBufferIn() {
        return bufferIn;
    }

    public void setBufferIn(BigDecimal bufferIn) {
        this.bufferIn = bufferIn;
    }

    public BigDecimal getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(BigDecimal setupTime) {
        this.setupTime = setupTime;
    }

    public BigDecimal getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(BigDecimal queueTime) {
        this.queueTime = queueTime;
    }

    public BigDecimal getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(BigDecimal productionTime) {
        this.productionTime = productionTime;
    }

    public boolean isProductionTimeFixed() {
        return isProductionTimeFixed;
    }

    public void setProductionTimeFixed(boolean productionTimeFixed) {
        isProductionTimeFixed = productionTimeFixed;
    }

    public BigDecimal getResourceTime() {
        return resourceTime;
    }

    public void setResourceTime(BigDecimal resourceTime) {
        this.resourceTime = resourceTime;
    }

    public BigDecimal getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(BigDecimal finishTime) {
        this.finishTime = finishTime;
    }

    public BigDecimal getBufferOut() {
        return bufferOut;
    }

    public void setBufferOut(BigDecimal bufferOut) {
        this.bufferOut = bufferOut;
    }

    public BigDecimal getOffsetTime() {
        return offsetTime;
    }

    public void setOffsetTime(BigDecimal offsetTime) {
        this.offsetTime = offsetTime;
    }

    public BigDecimal getMoveTime() {
        return moveTime;
    }

    public void setMoveTime(BigDecimal moveTime) {
        this.moveTime = moveTime;
    }
}
