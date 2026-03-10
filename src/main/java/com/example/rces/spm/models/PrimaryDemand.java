package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import java.time.LocalDateTime;

@Entity
@Subselect("SELECT * FROM dm_primarydemand")
@Immutable
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class PrimaryDemand {

    public enum DEMAND_TYPE {
        COL(), MPS, MRP, ROP, FCT, JOL, JCL, POL, SSO, ARL, IOL, LOG;
    }

    @Id
    private Long id;

    @Column(name = "storm_single_string")
    private String stormSingleString;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    private UnitMeasure unitmeasure;

    @Column(name = "date_due")
    private LocalDateTime dateDue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mlm_node_id")
    private MlmNode mlmNode;

    @ManyToOne(fetch = FetchType.LAZY)
    private SPMCustomerOrder customerorder;

    @Column(name="demand_type")
    @Enumerated(EnumType.STRING)
    private PrimaryDemand.DEMAND_TYPE demandType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStormSingleString() {
        return stormSingleString;
    }

    public void setStormSingleString(String stormSingleString) {
        this.stormSingleString = stormSingleString;
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

    public LocalDateTime getDateDue() {
        return dateDue;
    }

    public void setDateDue(LocalDateTime dateDue) {
        this.dateDue = dateDue;
    }

    public MlmNode getMlmNode() {
        return mlmNode;
    }

    public void setMlmNode(MlmNode mlmNode) {
        this.mlmNode = mlmNode;
    }

    public SPMCustomerOrder getCustomerorder() {
        return customerorder;
    }

    public void setCustomerorder(SPMCustomerOrder customerorder) {
        this.customerorder = customerorder;
    }

    public DEMAND_TYPE getDemandType() {
        return demandType;
    }

    public void setDemandType(DEMAND_TYPE demandType) {
        this.demandType = demandType;
    }
}
