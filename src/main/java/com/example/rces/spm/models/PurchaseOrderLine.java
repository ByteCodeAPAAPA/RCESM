package com.example.rces.spm.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Subselect("SELECT * FROM ppm_purchaseorderline")
@Immutable
public class PurchaseOrderLine extends PrimaryDemand {

    @ManyToOne(fetch= FetchType.LAZY)
    private PurchaseOrder purchaseorder;

    private Integer number;

    @Column(name = "date_due")
    private LocalDateTime localDateDue;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "date_actual_end")
    public LocalDateTime dateActualEnd;

    @Column(name = "is_vat_included")
    private Boolean isVatIncluded;

    public PurchaseOrder getPurchaseorder() {
        return purchaseorder;
    }

    public void setPurchaseorder(PurchaseOrder purchaseorder) {
        this.purchaseorder = purchaseorder;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public LocalDateTime getLocalDateDue() {
        return localDateDue;
    }

    public void setLocalDateDue(LocalDateTime localDateDue) {
        this.localDateDue = localDateDue;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateActualEnd() {
        return dateActualEnd;
    }

    public void setDateActualEnd(LocalDateTime dateActualEnd) {
        this.dateActualEnd = dateActualEnd;
    }

    public Boolean getVatIncluded() {
        return isVatIncluded;
    }

    public void setVatIncluded(Boolean vatIncluded) {
        isVatIncluded = vatIncluded;
    }
}
