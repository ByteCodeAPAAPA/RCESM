package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Subselect("SELECT * FROM jm_shift_task_line")
@Immutable
public class ShiftTaskLine {

    @Id
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createDate;

    @ManyToOne(fetch=FetchType.LAZY)
    private JobStep jobstep;

    @Column(name="qty_production", length=22, precision=20, scale=8)
    @ColumnDefault("0")
    private BigDecimal qtyProduction = BigDecimal.ZERO;  //План

    @Column(name="qty_finished", length=22, precision=20, scale=8)
    @ColumnDefault("0")
    private BigDecimal qtyFinished = BigDecimal.ZERO; //Выполнено

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getQtyProduction() {
        return qtyProduction;
    }

    public void setQtyProduction(BigDecimal qtyProduction) {
        this.qtyProduction = qtyProduction;
    }

    public BigDecimal getQtyFinished() {
        return qtyFinished;
    }

    public void setQtyFinished(BigDecimal qtyFinished) {
        this.qtyFinished = qtyFinished;
    }

    public JobStep getJobstep() {
        return jobstep;
    }

    public void setJobstep(JobStep jobstep) {
        this.jobstep = jobstep;
    }

}
