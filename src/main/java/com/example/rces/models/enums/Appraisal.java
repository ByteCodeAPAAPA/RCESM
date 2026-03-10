package com.example.rces.models.enums;

public enum Appraisal {

    score1(1, "Ужасно"),
    score2(2, "Плохо"),
    score3(3, "Удовлетворительно"),
    score4(4, "Хорошо"),
    score5(5, "Отлично");

    private final int id;
    private final String name;

    Appraisal(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Appraisal fromId(Integer score) {
        if (score == null) {
            return null;
        }
        for (Appraisal appraisal : Appraisal.values()) {
            if (appraisal.getId() == (score)) {
                return appraisal;
            }
        }
        throw new IllegalArgumentException("Неизвестная оценка: " + score);
    }
}