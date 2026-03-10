package com.example.rces.spm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM bpm_bprocessdocument")
@Immutable
public class BProcess {
    @Id
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    private Item item; //ТМЦ

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
