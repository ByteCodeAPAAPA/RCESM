package com.example.rces.models;

import com.example.rces.models.annotation.DisplayName;
import com.example.rces.models.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "requests", catalog = "rces")
@AuditTable(value = "requests_history", catalog = "rces_history")
public class Requests extends BaseAuditingEntity implements Cloneable {

    public enum Type {
        constructor("ОГК"),
        otk("ОТК"),
        technologist("ОГТ");

        private final String name;

        Type(String name) {
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

    @Column(name = "type_request")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Type typeRequest;

    @Column(name = "workDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DisplayName("Дата начала работы")
    private LocalDateTime dateWork;

    @Column(name = "request_number")
    private Integer requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @DisplayName("Ответственный")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @DisplayName("Заказ клиента")
    private CustomerOrder customerOrder;

    @Column(name = "reason")
    @Enumerated(EnumType.STRING)
    @DisplayName("Причина вызова")
    private GeneralReason reason;

    @Column(name = "qty")
    @DisplayName("Кол-во деталей к контролю")
    private Integer qty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_incosistencies",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "incosistency_id")
    )
    @DisplayName("Причины несоответствий")
    private Set<Inconsistency> inconsistencies;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subdivision_id", nullable = false)
    @NotNull(message = "Подразделение не может быть пустым")
    private SubDivision subDivision;

    @Column(name = "item")
    @Enumerated(EnumType.STRING)
    @DisplayName("Тип ТМЦ")
    private Item item;

    @Column(name = "status_id")
    @Enumerated(EnumType.STRING)
    @DisplayName("Статус")
    private Status status;

    @Column(name = "comment")
    @DisplayName("Комментарий")
    private String comment;

    @Column(name = "reason_wr")
    @DisplayName("Тип контроля")
    private String reason_wr;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    @DisplayName("Описание решения")
    private String description;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @DisplayName("Прикрепленные фото")
    @NotAudited
    private List<Images> images = new ArrayList<>();

    @Column(name = "closed_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DisplayName("Дата закрытия заявки")
    private LocalDateTime closeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_employee")
    @DisplayName("Закрывший заявку")
    private Employee closedEmployee;

    @Column(name = "chat_id")
    @DisplayName("Идентификатор чата ТГ")
    private Long chatId;

    @Column(name = "message_id")
    @DisplayName("Идентификатор сообщения")
    @NotAudited
    private Integer messageId;

    @Column(name = "score")
    @Enumerated(EnumType.STRING)
    @DisplayName("Оценка работы ответственного")
    private Appraisal score;

    @Column(name = "control")
    @DisplayName("Тип контроля")
    private String control;

    @Column(name = "comment_agreed")
    @DisplayName("Причина не согласования")
    private String commentAgreed;

    @Column(name = "title")
    @DisplayName("Обозначение/Наименование")
    private String title;

    @Column(name = "qty_rejected")
    @DisplayName("Забраковано")
    private int qtyRejected = 0;

    @Transient
    @DisplayName("Количество выполненного")
    private int qtyCompleted;

//    @Column(name = "notice")
//    @DisplayName("Уведомления")
//    private Boolean notice;

    //TODO че со старыми логами делать

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    @Deprecated(forRemoval = true)
    @NotAudited
    private List<RequestLog> log = new ArrayList<>();

    private boolean frozen;

//    public Boolean getNotice() {
//        return notice;
//    }
//
//    public void setNotice(Boolean notice) {
//        this.notice = notice;
//    }

    public int getQtyCompleted() {
        return qtyCompleted;
    }

    public void setQtyCompleted(int qtyCompleted) {
        this.qtyCompleted = qtyCompleted;
    }

    public int getQtyRejected() {
        return qtyRejected;
    }

    public void setQtyRejected(int qtyRejected) {
        this.qtyRejected = qtyRejected;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public String getCommentAgreed() {
        return commentAgreed;
    }

    public void setCommentAgreed(String commentAgreed) {
        this.commentAgreed = commentAgreed;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public String getReason_wr() {
        return reason_wr;
    }

    public void setReason_wr(String reason_wr) {
        this.reason_wr = reason_wr;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public Type getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(Type typeRequest) {
        this.typeRequest = typeRequest;
    }

    public LocalDateTime getDateWork() {
        return dateWork;
    }

    //Если старая версия не в работе и новая версия в работе
    public void setDateWork(LocalDateTime dateWork) {
        if (getVersion() <= 1 && status.equals(Status.InWork)) {
            this.dateWork = dateWork;
        }
    }

    public Integer getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Integer requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public GeneralReason getReason() {
        return reason;
    }

    public void setReason(GeneralReason reason) {
        this.reason = reason;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Set<Inconsistency> getInconsistencies() {
        return inconsistencies;
    }

    public void setInconsistencies(Set<Inconsistency> inconsistencies) {
        this.inconsistencies = inconsistencies;
    }

    public SubDivision getSubDivision() {
        return subDivision;
    }

    public void setSubDivision(SubDivision subDivision) {
        this.subDivision = subDivision;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (this.status != null) {
            if (this.status.equals(Status.Closed) || this.status.equals(Status.Cancel)) {
                closedEmployee = null;
                closeDate = null;
                messageId = null;
                score = null;
            }
        }
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public LocalDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDateTime closeDate) {
        this.closeDate = closeDate;
    }

    public Employee getClosedEmployee() {
        return closedEmployee;
    }

    public void setClosedEmployee(Employee closedEmployee) {
        this.closedEmployee = closedEmployee;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Appraisal getScore() {
        return score;
    }

    public void setScore(Appraisal score) {
        this.score = score;
    }

    @Deprecated(forRemoval = true)
    public List<RequestLog> getLog() {
        return log;
    }

    @Deprecated(forRemoval = true)
    public void setLog(List<RequestLog> log) {
        this.log = log;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
