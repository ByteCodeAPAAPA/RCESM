package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Subselect("SELECT * FROM jm_jobstep")
@Immutable
public class JobStep {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private JobComponent jobcomponent;

    @Column(name = "number")
    private Integer number;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mlm_node_id")
    private MlmNode mlmNode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Status status;

    @Column(name = "qty_production")
    private BigDecimal qtyProduction;  //План

    @Column(name = "qty_finished")
    private BigDecimal qtyFinished; //Выполнено

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Column(name = "date_end")
    private LocalDateTime dateEnd;

    @Column(name = "date_calc_start")
    private LocalDateTime dateCalcStart; //РД начала

    @Column(name = "date_calc_end")
    private LocalDateTime dateCalcEnd; //РД конец

    @Column(name = "date_actual_start")
    private LocalDateTime dateActualStart;  //ФД запуска

    @Column(name = "date_actual_end")
    private LocalDateTime dateActualEnd; //ФД конец

    @Column(name = "buffer_in")
    private BigDecimal bufferIn; //Буфер до

    @Column(name = "setup_time")
    private BigDecimal setupTime; //Время до

    @Column(name = "queue_time")
    private BigDecimal queueTime; //Время очереди

    @Column(name = "production_time")
    private BigDecimal productionTime; //Время производства

    @Column(name = "is_production_time_fixed")
    @ColumnDefault("false")
    private boolean isProductionTimeFixed = false; //Фиксированное время

    @Column(name = "resource_time")
    @ColumnDefault("0")
    private BigDecimal resourceTime; //Трудоемкость, шт

    @Column(name = "finish_time")
    private BigDecimal finishTime;  //Время после

    @Column(name = "buffer_out")
    private BigDecimal bufferOut;  //Буфер после

    @Column(name = "offset_time")
    private BigDecimal offsetTime; //Смещение

    @Column(name = "move_time")
    private BigDecimal moveTime; //Время перемещения

    @Column(name = "critical_ratio")
    private BigDecimal criticalRatio; //КО


    public String getFormattedDateStart() {
        return dateStart != null ? dateStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public String getFormattedDateEnd() {
        return dateEnd != null ? dateEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JobComponent getJobcomponent() {
        return jobcomponent;
    }

    public void setJobcomponent(JobComponent jobcomponent) {
        this.jobcomponent = jobcomponent;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getQtyProduction() {
        return qtyProduction.setScale(3, RoundingMode.DOWN);
    }

    public void setQtyProduction(BigDecimal qtyProduction) {
        this.qtyProduction = qtyProduction;
    }

    public BigDecimal getQtyFinished() {
        return qtyFinished.setScale(3, RoundingMode.DOWN);
    }

    public void setQtyFinished(BigDecimal qtyFinished) {
        this.qtyFinished = qtyFinished;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateTime getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDateTime dateEnd) {
        this.dateEnd = dateEnd;
    }

    public LocalDateTime getDateCalcStart() {
        return dateCalcStart;
    }

    public void setDateCalcStart(LocalDateTime dateCalcStart) {
        this.dateCalcStart = dateCalcStart;
    }

    public LocalDateTime getDateCalcEnd() {
        return dateCalcEnd;
    }

    public void setDateCalcEnd(LocalDateTime dateCalcEnd) {
        this.dateCalcEnd = dateCalcEnd;
    }

    public LocalDateTime getDateActualStart() {
        return dateActualStart;
    }

    public void setDateActualStart(LocalDateTime dateActualStart) {
        this.dateActualStart = dateActualStart;
    }

    public LocalDateTime getDateActualEnd() {
        return dateActualEnd;
    }

    public void setDateActualEnd(LocalDateTime dateActualEnd) {
        this.dateActualEnd = dateActualEnd;
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
        return resourceTime.setScale(3, RoundingMode.DOWN);
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

    public BigDecimal getCriticalRatio() {
        return criticalRatio;
    }

    public void setCriticalRatio(BigDecimal criticalRatio) {
        this.criticalRatio = criticalRatio;
    }
}
