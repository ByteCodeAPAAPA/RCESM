package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Subselect("SELECT * FROM jm_jobcomponent")
@Immutable
public class JobComponent {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PrimaryDemand primarydemand;

    @ManyToOne(fetch = FetchType.LAZY)
    private JobStep jobstep;

    @Column(name = "number")
    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    private UnitMeasure unitmeasure;

    @Column(name = "qty_demand")
    private BigDecimal qtyDemand; //План брутто

    @Column(name = "qty_required")
    private BigDecimal qtyRequired; //Потребность

    @Column(name = "qty_finished")
    private BigDecimal qtyFinished; //Выполнено

    @Column(name = "bom_level")
    private Integer bomLevel;

    @Column(name = "qty_bom")
    private BigDecimal qtyBom; //Количество

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_jobcomponent_id")
    private JobComponent parentJobComponent;//Входит в компонент

    @OneToMany(mappedBy = "parentJobComponent", fetch = FetchType.EAGER)
    @BatchSize(size = 20)
    private List<JobComponent> childJCList;

    @OneToMany(mappedBy = "jobcomponent", fetch = FetchType.LAZY)
    @OrderBy("jobcomponent, number")
    private List<JobStep> jobSteps; //Заходы

    @Column(name = "date_start")
    private LocalDateTime dateStart;

    @Transient
    private LocalDateTime dateEnd;

    @Column(name = "date_calc_end")
    private LocalDateTime dateCalcEnd; //РД завершения

    public String getFormattedDateStart() {
        return dateStart != null ? dateStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public String getFormattedDateCalcEnd() {
        return dateCalcEnd != null ? dateCalcEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrimaryDemand getPrimarydemand() {
        return primarydemand;
    }

    public void setPrimarydemand(PrimaryDemand primarydemand) {
        this.primarydemand = primarydemand;
    }

    public JobStep getJobstep() {
        return jobstep;
    }

    public void setJobstep(JobStep jobstep) {
        this.jobstep = jobstep;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public UnitMeasure getUnitmeasure() {
        return unitmeasure;
    }

    public void setUnitmeasure(UnitMeasure unitmeasure) {
        this.unitmeasure = unitmeasure;
    }

    public BigDecimal getQtyDemand() {
        return qtyDemand;
    }

    public void setQtyDemand(BigDecimal qtyDemand) {
        this.qtyDemand = qtyDemand;
    }

    public BigDecimal getQtyRequired() {
        return qtyRequired;
    }

    public void setQtyRequired(BigDecimal qtyRequired) {
        this.qtyRequired = qtyRequired;
    }

    public BigDecimal getQtyFinished() {
        return qtyFinished;
    }

    public void setQtyFinished(BigDecimal qtyFinished) {
        this.qtyFinished = qtyFinished;
    }

    public Integer getBomLevel() {
        return bomLevel;
    }

    public void setBomLevel(Integer bomLevel) {
        this.bomLevel = bomLevel;
    }

    public BigDecimal getQtyBom() {
        return qtyBom;
    }

    public void setQtyBom(BigDecimal qtyBom) {
        this.qtyBom = qtyBom;
    }

    public JobComponent getParentJobComponent() {
        return parentJobComponent;
    }

    public void setParentJobComponent(JobComponent parentJobComponent) {
        this.parentJobComponent = parentJobComponent;
    }

    public List<JobComponent> getChildJCList() {
        return childJCList;
    }

    public void setChildJCList(List<JobComponent> childJCList) {
        this.childJCList = childJCList;
    }

    public List<JobStep> getJobSteps() {
        return jobSteps;
    }

    public void setJobSteps(List<JobStep> jobSteps) {
        this.jobSteps = jobSteps;
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

    public LocalDateTime getDateCalcEnd() {
        return dateCalcEnd;
    }

    public void setDateCalcEnd(LocalDateTime dateCalcEnd) {
        this.dateCalcEnd = dateCalcEnd;
    }
}
