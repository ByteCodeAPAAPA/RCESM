package com.example.rces.spm.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM bpm_bprocessdocumentline")
@Immutable
public class BProcessLine {

    @Id
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    private BProcess bprocessdocument;

    public BProcess getBprocessdocument() {
        return bprocessdocument;
    }

    public void setBprocessdocument(BProcess bprocessdocument) {
        this.bprocessdocument = bprocessdocument;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
