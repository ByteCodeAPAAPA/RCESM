//Класс с причинами вызовов
package com.example.rces.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

public enum GeneralReason {

    tech1("Отработка технологического изготовления", "technologist"),
    tech2("Оказание помощи производству", "technologist"),
    tech3("Освоение новых технологий", "technologist"),
    cons1("Неполнота данных в ркд", "constructor"),
    cons2("Ошибка исполнителя", "constructor"),
    cons3("Требуется корректировка ркд", "constructor"),
    otk1("ВИК", "otk"),
    otk2("УДС", "otk"),
    otk3("УЗД", "otk");

    private final String name;

    private final String requestType;

    GeneralReason(String name, String requestType) {
        this.name = name;
        this.requestType = requestType;
    }

    public String getName() {
        return name;
    }

    public String getRequestType() {
        return requestType;
    }

    @JsonCreator
    public static GeneralReason fromField(JsonNode node) {
        if (node == null) {
            return null;
        }

        if (node.has("name") && node.get("name").isTextual()) {
            String name = node.get("name").asText();
            for (GeneralReason reason : GeneralReason.values()) {
                if (reason.getName().equals(name)) {
                    return reason;
                }
            }
        }

        throw new IllegalArgumentException("Неизвестная причина: " + node);
    }
}
