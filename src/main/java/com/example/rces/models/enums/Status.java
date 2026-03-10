//Все статусы
package com.example.rces.models.enums;

public enum Status {

    New(1L, "Новый"),
    InWork(2L, "В работе"),
    Closed(3L, "Закрыт"),
    Cancel(4L, "Отменен"),
    Completed(5L, "Выполнен"),
    Rejected(6L,"Забракована"),
    UnderRework(7L,"На доработке");

    private final Long id;
    private final String name;

    Status(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Status fromField(Object field) {
        if (field == null) {
            return null;
        }
        String fieldAsString = field.toString();
        for (Status status : Status.values()) {
            if (status.name().equals(fieldAsString)) {
                return status;
            }
        }
        for (Status status : Status.values()) {
            if (status.getName().equals(fieldAsString)) {
                return status;
            }
        }
        if (field instanceof Number) {
            for (Status status : Status.values()) {
                if (status.getId().equals(field)) {
                    return status;
                }
            }
        }

        throw new IllegalArgumentException("Неизвестный статус: " + field);
    }
}
