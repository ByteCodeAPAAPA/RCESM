package com.example.rces.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rces_machine", catalog = "rces")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "number", unique = true, nullable = false)
    private Integer number;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "machine_admitted_employees",
            joinColumns = @JoinColumn(name = "machine_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> admittedEmployees = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "machine_responsible_employees",
            joinColumns = @JoinColumn(name = "machine_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> responsibleEmployees = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public List<Employee> getAdmittedEmployees() {
        return admittedEmployees;
    }

    public void setAdmittedEmployees(List<Employee> admittedEmployees) {
        this.admittedEmployees = admittedEmployees;
    }

    public List<Employee> getResponsibleEmployees() {
        return responsibleEmployees;
    }

    public void setResponsibleEmployees(List<Employee> responsibleEmployees) {
        this.responsibleEmployees = responsibleEmployees;
    }

    public void addAdmittedEmployees(Employee employee) {
        this.admittedEmployees.add(employee);
    }

    public void addResponsibleEmployees(Employee employee) {
        this.responsibleEmployees.add(employee);
    }
}
