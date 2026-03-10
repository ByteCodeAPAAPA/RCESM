package com.example.rces.spm.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.util.List;

@Entity
@Subselect("select * from ppm_purchaseorder")
@Immutable
public class PurchaseOrder {

    @Id
    private Long id;

    @Column(name="str_code")
    private String code; //Номер

    @OneToMany(mappedBy="purchaseorder")
    private List<PurchaseOrderLine> lines;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<PurchaseOrderLine> getLines() {
        return lines;
    }

    public void setLines(List<PurchaseOrderLine> lines) {
        this.lines = lines;
    }
}
