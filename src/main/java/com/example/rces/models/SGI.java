package com.example.rces.models;

import com.example.rces.models.annotation.DisplayName;
import com.example.rces.models.enums.Color;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "plan_sgi", catalog = "rces")
@AuditTable(value = "plan_sgi_history", catalog = "rces_history")
@NamedEntityGraph(
        name = "SGI.withAssociations",
        attributeNodes = {
                @NamedAttributeNode("executions"),
                @NamedAttributeNode("images"),
                @NamedAttributeNode("employee")
        }
)
@BatchSize(size = 20)
public class SGI extends BaseAuditingEntity implements Cloneable {

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public enum Department {
        mechanic("ОГМ"), builder("ОРС"), protection("ОТиПК"), energy("ОГЭ");

        private final String name;

        Department(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Department fromName(String name) {
            for (Department department : Department.values()) {
                if (department.name.equals(name)) {
                    return department;
                }
            }
            throw new IllegalArgumentException("Неизвестный отдел: " + name);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "number")
    private int requestNumber;

    @Column(name = "workshop")
    @DisplayName("№ цеха")
    private String workShop;

    @Column(name = "event")
    @DisplayName("Мероприятие")
    private String event;

    @Column(name = "actions", length = 499)
    @DisplayName("Действия")
    private String actions;

    @Column(name = "department")
    @Enumerated(EnumType.STRING)
    @DisplayName("Ответственный отдел")
    private SGI.Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @DisplayName("Ответственный сотрудник")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_sgi_id")
    @DisplayName("Родительская задача")
    private SGI parentSGI;

    @OneToMany(mappedBy = "parentSGI", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @DisplayName("Подзадачи")
    @BatchSize(size = 20)
    private List<SGI> subSGI = new ArrayList<>();

    @Column(name = "desired_date")
    @DisplayName("Желаемая дата")
    private LocalDate desiredDate;

    @Column(name = "plan_date")
    @DisplayName("Планируемая дата")
    private LocalDate planDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @DisplayName("Факт выполнения")
    private FactExecutionSGI executions;

    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(name = "note", length = 1000)
    @DisplayName("Примечание")
    private String note;

    @Column(name = "comment", length = 1000)
    @DisplayName("Комментарий")
    private String comment;

    @OneToMany(mappedBy = "sgim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @DisplayName("Прикрепленные фото")
    @BatchSize(size = 20)
    @NotAudited
    private List<Images> images = new ArrayList<>();

    @Column(name = "agreed")
    @DisplayName("Согласовано")
    private Boolean agreed;

    //TODO че со старыми логами делать

    @OneToMany(mappedBy = "sgi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Deprecated(forRemoval = true)
    @NotAudited
    private List<SgiLog> log = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getWorkShop() {
        return workShop;
    }

    public void setWorkShop(String workShop) {
        this.workShop = workShop;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public SGI getParentSGI() {
        return parentSGI;
    }

    public void setParentSGI(SGI parentSGI) {
        this.parentSGI = parentSGI;
    }

    public List<SGI> getSubSGI() {
        return subSGI;
    }

    public void setSubSGI(List<SGI> subSGI) {
        this.subSGI = subSGI;
    }

    public LocalDate getDesiredDate() {
        return desiredDate;
    }

    public void setDesiredDate(LocalDate desiredDate) {
        this.desiredDate = desiredDate;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public FactExecutionSGI getExecution() {
        return executions;
    }

    public void setExecution(FactExecutionSGI executions) {
        this.executions = executions;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public Boolean getAgreed() {
        return agreed;
    }

    public void setAgreed(Boolean agreed) {
        this.agreed = agreed;
    }

    public List<SgiLog> getLog() {
        return log;
    }

    @Deprecated(forRemoval = true)
    public void setLog(List<SgiLog> log) {
        this.log = log;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
