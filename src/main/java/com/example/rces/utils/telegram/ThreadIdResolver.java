package com.example.rces.utils.telegram;

class ThreadIdResolver {
    public static Integer resolve(String department) {
        return switch (department) {
            case "ОГЭ" -> 6;
            case "ОТиПК" -> 4;
            case "ОРС" -> 3;
            case "ОГМ" -> 2;
            default -> null;
        };
    }
}