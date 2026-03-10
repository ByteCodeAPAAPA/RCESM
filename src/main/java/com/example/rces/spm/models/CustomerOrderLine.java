package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM dm_customerorderline")
@Immutable
public class CustomerOrderLine extends PrimaryDemand {

    @ManyToOne(fetch= FetchType.LAZY)
    private SPMCustomerOrder customerorder;

    @Column(name = "number")
    private Integer number;

    public SPMCustomerOrder getCustomerorder() {
        return customerorder;
    }

    public void setCustomerorder(SPMCustomerOrder customerorder) {
        this.customerorder = customerorder;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
