package com.example.rces.spm.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

@Entity
@Subselect("SELECT * FROM mlm_site")
@Immutable
public class Site {

    @Id
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        if (getId() == 15841035L) {
            return "Несуществующий узел";
        } else if (getId() == 46750L) {
            return "БорМаш";
        } else if (getId() == 172795L) {
            return "Кооператор";
        } else {
            return "Завод БорМаш";
        }
    }

}
