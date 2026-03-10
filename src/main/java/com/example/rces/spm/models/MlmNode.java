package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM mlm_node")
@Immutable
public class MlmNode {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch= FetchType.LAZY)
    private StockNode releaseStockNode; //Склад выпуска

    public StockNode getReleaseStockNode() {
        return releaseStockNode;
    }

    public void setReleaseStockNode(StockNode releaseStockNode) {
        this.releaseStockNode = releaseStockNode;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
