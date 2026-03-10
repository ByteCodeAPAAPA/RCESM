package com.example.rces.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inspection_violation", catalog = "rces")
@NamedQueries({
        @NamedQuery(
                name = "InspectionViolation.findAllNotFixed",
                query = """
                        SELECT i FROM InspectionViolation i
                        LEFT JOIN FETCH i.subDivision s
                        LEFT JOIN FETCH i.createdBy cb
                        LEFT JOIN FETCH cb.subDivision cbs
                        LEFT JOIN FETCH i.inspection ins
                        WHERE s.code in ('PDO', 'OGM', 'OTTB', 'OGT') and i.status = 'status1'
                        """
        )
})
public class InspectionViolation extends BaseAuditingEntity {

    public enum CriteriaInspection {
        criteria1("Безопасность и охрана труда"), criteria2("Технологическая дисциплина"),
        criteria3("Организация рабочих мест"), criteria4("Документация");

        private final String name;

        CriteriaInspection(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
        public static CriteriaInspection getByName(String name) {
            return Arrays.stream(CriteriaInspection.values()).filter(criteriaInspection -> criteriaInspection.getName().equals(name)).findFirst().orElse(null);
        }
    }

    public enum StatusInspection {
        status1("Не исправлено"), status2("Исправлено");

        private final String name;

        StatusInspection(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "criteria")
    @Enumerated(EnumType.STRING)
    private InspectionViolation.CriteriaInspection criteria;

    @Column(name = "score")
    @Min(value = 1, message = "Оценка должна быть не меньше 1")
    @Max(value = 100, message = "Оценка должна быть не больше 100")
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Ответственное подразделение не может быть пустым")
    @JoinColumn(name = "sub_division_id", nullable = false)
    private SubDivision subDivision;

    @OneToMany(mappedBy = "insVio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Images> images = new ArrayList<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InspectionViolation.StatusInspection status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Inspection getInspection() {
        return inspection;
    }

    public void setInspection(Inspection inspection) {
        this.inspection = inspection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCriteria() {
        return criteria.getName();
    }

    public CriteriaInspection getCriteriaInspection() {
        return criteria;
    }

    public void setCriteria(CriteriaInspection criteria) {
        this.criteria = criteria;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public SubDivision getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivision subDivision) {
        this.subDivision = subDivision;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public String getStatus() {
        return status.getName();
    }

    public StatusInspection getStatusInspection() {
        return status;
    }

    public void setStatus(StatusInspection status) {
        this.status = status;
    }
}
