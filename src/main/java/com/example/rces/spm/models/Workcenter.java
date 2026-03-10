package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM mlm_workcenter")
@Immutable
public class Workcenter {

    @Id
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    private MlmNode mlmNode; //Узел ПЛМ

    @ManyToOne(fetch=FetchType.LAZY)
    private Employee foreman; //Мастер участка

    public MlmNode getMlmNode() {
        return mlmNode;
    }

    public void setMlmNode(MlmNode mlmNode) {
        this.mlmNode = mlmNode;
    }

    public Employee getForeman() {
        return foreman;
    }

    public void setForeman(Employee foreman) {
        this.foreman = foreman;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
