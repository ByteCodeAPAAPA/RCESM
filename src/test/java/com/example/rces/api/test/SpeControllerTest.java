package com.example.rces.api.test;

import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.SeverityLevel.CRITICAL;

public class SpeControllerTest {

    private Integer createdSpeId;

    @AfterEach
    void cleanUp() {
        if (createdSpeId != null) {

        }
    }

    @Test
    @DisplayName("Полный жизненный цикл перечня оборудования")
    @Owner("ByteCodeAPAA")
    @Severity(CRITICAL)
    public void fullSpeLifecycle() {

    }



}
