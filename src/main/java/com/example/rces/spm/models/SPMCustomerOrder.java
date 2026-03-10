package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Subselect("SELECT * FROM dm_customerorder")
@Immutable
public class SPMCustomerOrder {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "str_code")
    private String strCode;

    @ManyToOne(fetch=FetchType.LAZY)
    private Site site;

    @Column(name="date_due")
    private LocalDateTime dateDue;

    @Column(name="date_contract")
    private LocalDateTime dateContract;

    @OneToMany(mappedBy="customerorder", fetch = FetchType.LAZY)
    private List<CustomerOrderLine> lines;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrCode() {
        return strCode;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public LocalDateTime getDateDue() {
        return dateDue;
    }

    public void setDateDue(LocalDateTime dateDue) {
        this.dateDue = dateDue;
    }

    public LocalDateTime getDateContract() {
        return dateContract;
    }

    public void setDateContract(LocalDateTime dateContract) {
        this.dateContract = dateContract;
    }

    public List<CustomerOrderLine> getLines() {
        return lines;
    }

    public void setLines(List<CustomerOrderLine> lines) {
        this.lines = lines;
    }
}
