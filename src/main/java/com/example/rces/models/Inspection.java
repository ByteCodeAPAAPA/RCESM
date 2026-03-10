package com.example.rces.models;

import com.example.rces.models.annotation.DisplayName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "inspection", catalog = "rces")
@AuditTable(value = "inspection_history", catalog = "rces_history")
public class Inspection extends BaseAuditingEntity implements Cloneable{

    public enum TypeInspection {
        primary("Первичная"), secondary("Вторичная");

        private final String name;

        TypeInspection(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "number")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Цех/Подразделение не может быть пустым")
    @JoinColumn(name = "sub_division_id",nullable = false)
    private SubDivision subDivision;

    @Column(name = "date_inspection")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateInspection;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Inspection.TypeInspection type;

    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @DisplayName("Нарушения")
    @BatchSize(size = 20)
    @NotAudited
    private List<InspectionViolation> violation = new ArrayList<>();

    @Column(name = "have_second_inspection")
    private Boolean haveSecondInspection = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_inspection_id")
    private Inspection primaryInspection;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SubDivision getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivision subDivision) {
        this.subDivision = subDivision;
    }

    public LocalDateTime getDateInspection() {
        return dateInspection;
    }

    public void setDateInspection(LocalDateTime dateInspection) {
        this.dateInspection = dateInspection;
    }

    public String getType() {
        return type.getName();
    }

    public void setType(TypeInspection type) {
        this.type = type;
    }

    public List<InspectionViolation> getViolation() {
        return violation;
    }

    public void setViolation(List<InspectionViolation> violation) {
        this.violation = violation;
    }

    public Boolean getHaveSecondInspection() {
        return haveSecondInspection;
    }

    public void setHaveSecondInspection(Boolean haveSecondInspection) {
        this.haveSecondInspection = haveSecondInspection;
    }

    public Inspection getPrimaryInspection() {
        return primaryInspection;
    }

    public void setPrimaryInspection(Inspection primaryInspection) {
        this.primaryInspection = primaryInspection;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
