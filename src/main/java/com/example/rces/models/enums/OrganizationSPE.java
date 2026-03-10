package com.example.rces.models.enums;

import com.example.rces.exception.ResourceNotFoundException;

public enum OrganizationSPE {

    organization1("Борисоглебский филиал ФБУ \"Воронежский ЦСМ\"", "Директор", "Д.Н.Колодяжный"),
    organization2("ФБУ \"Воронежский ЦСМ\"", "Зам.директора", "П.В.Воронин"),
    organization3("ООО \"СТАНДАРТ\"", "Директор", "В.Ю.Букреев");


    OrganizationSPE(String name, String position, String verifier) {
        this.name = name;
        this.position = position;
        this.verifier = verifier;
    }

    private String name;

    private String position;

    private String verifier;

    public static OrganizationSPE fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        String normalizedInput = input.toLowerCase().trim();
        if (normalizedInput.contains("борисоглебский") && normalizedInput.contains("филиал")) {
            return organization1;
        }  else if (normalizedInput.contains("воронежский") && normalizedInput.contains("цсм")) {
            return organization2;
        } else if (normalizedInput.contains("стандарт") || normalizedInput.contains("ооо")) {
            return organization3;
        }
        throw new ResourceNotFoundException("Неизвестная организация: " + input, NotificationType.ERROR);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
