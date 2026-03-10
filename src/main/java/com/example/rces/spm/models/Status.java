package com.example.rces.spm.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM mdm_status")
@Immutable
public class Status {

    @Id
    private Long id;

    @Column(name = "is_new")
    private Boolean isNew = false;

    @Column(name = "is_approving")
    private Boolean isApproving = false;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Column(name = "is_processing")
    private Boolean isProcessing = false;

    @Column(name = "is_stopped")
    private Boolean isStopped = false;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public Boolean getApproving() {
        return isApproving;
    }

    public void setApproving(Boolean approving) {
        isApproving = approving;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Boolean getProcessing() {
        return isProcessing;
    }

    public void setProcessing(Boolean processing) {
        isProcessing = processing;
    }

    public Boolean getStopped() {
        return isStopped;
    }

    public void setStopped(Boolean stopped) {
        isStopped = stopped;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }
}
