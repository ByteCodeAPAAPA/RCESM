package com.example.rces.spm.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDateTime;

@Entity
@Subselect("SELECT * FROM jm_joborder")
@Immutable
public class JobOrder extends PrimaryDemand {

    @Column(name = "str_code")
    public String strCode;

    @Column(name = "description")
    public String description;

    @Column(name = "date_start")
    public LocalDateTime date_start;

    @Column(name="date_actual_start")
    private LocalDateTime dateActualStart; //ФД запуска

    @Column(name="date_actual_end")
    private LocalDateTime dateActualEnd; //ФД завершения

    @Formula("(SELECT jov.date_calc_end FROM jobordervisible_view jov WHERE jov.id = id)")
    private LocalDateTime dateCalcEnd;

    public String getStrCode() {
        return strCode;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate_start() {
        return date_start;
    }

    public void setDate_start(LocalDateTime date_start) {
        this.date_start = date_start;
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

    public LocalDateTime getDateCalcEnd() {
        return dateCalcEnd;
    }

    public void setDateCalcEnd(LocalDateTime dateCalcEnd) {
        this.dateCalcEnd = dateCalcEnd;
    }
}
