//сущность заказы клиентов
package com.example.rces.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.UUID;

@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "customerorder", catalog = "rces")
@AuditTable(value = "customerorder_history", catalog = "rces_history")
public class CustomerOrder extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "str_code", length = 100, nullable = false)
    @NotBlank(message = "Наименование заказа не может быть пустым")
    @Size(min = 1, max = 100, message = "Наименование заказа должно содержать от 1 до 100 символов")
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
