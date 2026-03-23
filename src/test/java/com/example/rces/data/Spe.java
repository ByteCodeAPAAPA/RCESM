package com.example.rces.data;

import com.example.rces.dto.SpeCreateDTO;
import com.example.rces.models.enums.OrganizationSPE;

import java.time.LocalDate;

import static com.example.rces.data.Employee.admin_user;
import static com.example.rces.data.SubDivision.empty_subDivision;

public class Spe {

    public static SpeCreateDTO createTestSpeDto() {
        return SpeCreateDTO.builder()
                .name("Тестовое оборудование")
                .type("тест")
                .outNumber("123456")
                .accuracyClass("0.1")
                .limitMeasurement("0-250мм")
                .subDivision(empty_subDivision)
                .employee(admin_user)
                .periodicity(12)
                .datePreparation(LocalDate.now())
                .dateVerification(LocalDate.now())
                .certificateNumber("123456789")
                .organization(OrganizationSPE.organization1)
                .build();
    }

}
