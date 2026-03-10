package com.example.rces.models;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "executed_run_once_scripts", catalog = "rces_history")
@NamedQuery(
        name = "ExecutedRunOnceScripts.findByName",
        query = "select count(*) from ExecutedRunOnceScripts e where e.name = :p0"
)
public class ExecutedRunOnceScripts {
    public static final String findByName_query = "ExecutedRunOnceScripts.findByName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @CreatedDate
    private Instant executionDate;

    public ExecutedRunOnceScripts() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Instant executionDate) {
        this.executionDate = executionDate;
    }
}
