package com.example.rces.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

public enum Item {

    Item1("Всп. материал"),
    Item2("Двигатель"),
    Item3("Днища"),
    Item4("Заготовка"),
    Item5("Инструмент"),
    Item6("Крепеж"),
    Item7("Труба"),
    Item8("Фланцы,Полукольца"),
    Item9("Тройники,отводы"),
    Item10("Стенка боковая секции"),
    Item11("Металлоконструкция"),
    Item12("Деталь"),
    Item13("Другие комплектующие,прочие устройства"),
    Item14("КИП,ШУ"),
    Item15("Компенсатор,конус"),
    Item16("Финальная сборка"),
    Item17("Узловая сборка"),
    Item18("Услуги на стороне"),
    Item19("Прокладки"),
    Item20("Оснастка"),
    Item21("ЛКП"),
    Item22("Листовой прокат"),
    Item23("Секция"),
    Item24("Теплообменный аппарат"),
    Item25("Карта раскроя"),
    Item26("Ящик ЗИП"),
    Item27("ШПУ"),
    Item28("Ящик с паспортом");

    private final String name;

    Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static Item fromField(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (node.has("name") && node.get("name").isTextual()) {
            String name = node.get("name").asText();
            for (Item item : Item.values()) {
                if (item.getName().equals(name)) {
                    return item;
                }
            }
        }
        throw new IllegalArgumentException("Неизвестная причина: " + node);
    }
}
