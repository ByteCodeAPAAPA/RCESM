package com.example.rces.models;

import com.example.rces.models.enums.Color;
import com.example.rces.models.enums.OrganizationSPE;
import com.example.rces.models.enums.StatusSPE;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDate;

@Entity
@DynamicUpdate
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "plan_spe", catalog = "rces")
@AuditTable(value = "plan_spe_history", catalog = "rces_history")
public class SPE extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "number")
    private Integer number;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Наименование оборудования не может быть пустым")
    @Size(min = 1, message = "Наименование оборудования должно содержать минимум 6 символов")
    private String name;

    @Column(name = "type")
    @NotBlank(message = "Тип оборудования не может быть пустым")
    @Size(min = 1, message = "Тип оборудования должно содержать минимум 1 символ")
    private String type;

    @Column(name = "out_number")
    @NotBlank(message = "Заводской номер не может быть пустым")
    @Size(min = 1, message = "Заводской номер должно содержать минимум 3 символа")
    private String outNumber;

    @Column(name = "accuracy_class")
    private String accuracyClass;

    @Column(name = "limit_measurement")
    private String limitMeasurement;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Подразделение не может быть пустым")
    @JoinColumn(name = "sub_division_id", nullable = false)
    private SubDivision subDivision;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Сотрудник не может быть пустым")
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "mark")
    private String mark;

    @Column(name = "date_preparation")
    private LocalDate datePreparation;

    @Column(name = "date_verification")
    private LocalDate dateVerification;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "periodicity")
    @Min(value = 1, message = "Периодичность поверки должна быть не менее 1 символа")
    @Max(value = 100, message = "Периодичность поверки должна быть не более 2 символов")
    private Integer periodicity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusSPE status = StatusSPE.NONE;

    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private Color color = Color.NONE;

    @Column(name = "organization")
    @Enumerated(EnumType.STRING)
    private OrganizationSPE organization;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOutNumber() {
        return outNumber;
    }

    public void setOutNumber(String outNumber) {
        this.outNumber = outNumber;
    }

    public String getAccuracyClass() {
        return accuracyClass;
    }

    public void setAccuracyClass(String accuracyClass) {
        this.accuracyClass = accuracyClass;
    }

    public String getLimitMeasurement() {
        return limitMeasurement;
    }

    public void setLimitMeasurement(String limitMeasurement) {
        this.limitMeasurement = limitMeasurement;
    }

    public SubDivision getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivision subDivision) {
        this.subDivision = subDivision;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public LocalDate getDatePreparation() {
        return datePreparation;
    }

    public void setDatePreparation(LocalDate datePreparation) {
        this.datePreparation = datePreparation;
    }

    public LocalDate getDateVerification() {
        return dateVerification;
    }

    public void setDateVerification(LocalDate dateVerification) {
        this.dateVerification = dateVerification;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public Integer getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(Integer periodicity) {
        this.periodicity = periodicity;
    }

    public StatusSPE getStatus() {
        return status;
    }

    public void setStatus(StatusSPE status) {
        this.status = status;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public OrganizationSPE getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationSPE organization) {
        this.organization = organization;
    }
}
