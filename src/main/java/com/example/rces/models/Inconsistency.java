package com.example.rces.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "inconsistencies", catalog = "rces")
@AuditTable(value = "inconsistencies_history", catalog = "rces_history")
public class Inconsistency extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", unique = true)
    private String name;

    @NotBlank
    @Column(name = "control_type")
    private String controlType;

    public Inconsistency() {
    }

    public Inconsistency(String name, String controlType) {
        this.name = name;
        this.controlType = controlType;
    }

    public static Set<Inconsistency> fromField(Object field, Set<Inconsistency> allInconsistencies) {
        try {
            if (field == null) {
                return new HashSet<>();
            }

            String input = field.toString();
            Pattern pattern = Pattern.compile("\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(input);

            Set<String> foundNames = new HashSet<>();
            while (matcher.find()) {
                String match = matcher.group(1);
                if (!match.trim().equals("name") && !match.trim().isEmpty()) {
                    foundNames.add(match.trim());
                }
            }

            // Фильтруем из всех доступных несоответствий
            return allInconsistencies.stream()
                    .filter(inc -> foundNames.contains(inc.getName()))
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            throw new RuntimeException("Error parsing inconsistencies from field", e);
        }
    }

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

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

}
