package com.example.rces.api.test;

import com.example.rces.dto.SgiDTO;
import groovy.util.logging.Slf4j;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.UUID;

import static com.example.rces.api.steps.SgiControllerSteps.*;
import static com.example.rces.data.Sgi.createTestSgiDto;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Feature("Api")
@Story("Api Мероприятия")
@Tags({@Tag("SgiController"), @Tag("api")})
@DisplayName("Мероприятия (API)")
@Slf4j
public class SgiControllerTest extends BaseApiTest {

    private UUID createdSgiId;

    @AfterEach
    void cleanUp() {
        if (createdSgiId != null) {
            deleteSgiById(createdSgiId);
        }
    }

    @Test
    @DisplayName("Полный жизненный цикл мероприятия")
    @Owner("ByteCodeAPAA")
    @Severity(CRITICAL)
    public void fullSgiLifecycle() {
        String uniqueEventName = "Тестовое мероприятие " + UUID.randomUUID();

        try {
            step("Создание мероприятия");
            SgiDTO sgi = createSGI(createTestSgiDto(uniqueEventName));
            createdSgiId = sgi.getId();
            assertThat(sgi.getId()).isNotNull();

            step("Добавление плановой даты");
            sgi = addPlanDate(sgi);
            assertThat(sgi.getPlanDate()).isEqualTo(LocalDate.now());

            step("Добавление факта выполнения");
            String report = "Тестовый отчет " + UUID.randomUUID();
            sgi = addFactExecution(sgi.getId(), LocalDate.now(), report);
            assertThat(sgi.getFactExecution().getReport()).isEqualTo(report);

            step("Согласование мероприятия");
            assertThat(agreeEvent(sgi.getId(), true)).isTrue();

            step("Отмена согласования");
            agreeEvent(sgi.getId(), false);
            sgi = getById(sgi.getId());
            assertThat(sgi.getAgree()).isFalse();

        } catch (AssertionError | Exception e) {
            stepFailed(e);
            throw e;
        }
    }
}
