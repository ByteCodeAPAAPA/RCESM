package com.example.rces.api.test;

import com.example.rces.dto.InspectionCreateDTO;
import com.example.rces.dto.InspectionDTO;
import com.example.rces.dto.InspectionViolationCreateDTO;
import com.example.rces.dto.InspectionViolationDTO;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

import static com.example.rces.api.steps.InspectionControllerSteps.*;
import static com.example.rces.data.Inspection.createTestInspectionDTO;
import static com.example.rces.data.Inspection.createTestViolationDTO;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Feature("Api")
@Story("Api Инспекции")
@Tags({@Tag("InspectionController"), @Tag("api")})
@DisplayName("Инспекции (API)")
@Slf4j
public class InspectionControllerTest extends BaseApiTest {

    private Integer createdInspectionId;
    private Integer secondaryInspectionId;
    public static final String STATUS_FIXED = "Исправлено";

    @AfterEach
    void cleanUp() {
        if (secondaryInspectionId != null) {
            deleteInspection(secondaryInspectionId, 204);
        }
        if (createdInspectionId != null) {
            deleteInspection(createdInspectionId, 204);
        }
    }

    @ParameterizedTest
    @DisplayName("Полный api цикл работы с инспекцией: создание → редактирование → выполнение → удаление")
    @Owner("ByteCodeAPAA")
    @Severity(CRITICAL)
    @MethodSource("com.example.rces.data.SubDivisionTestHelper#getRandomSubDivisionNameStream")
    public void fullInspectionLifecycle(String subDivision) {
        String testId = UUID.randomUUID().toString().substring(0, 8);

        log.info("🚀 Запуск теста для подразделения: '{}' [testId: {}]", subDivision, testId);

        try {
            step(String.format("Создание DTO для инспекции (подразделение: %s)", subDivision));
            InspectionCreateDTO inspectionCreateDTO = createTestInspectionDTO(subDivision);

            step("Создание инспекции");
            InspectionDTO inspection = createInspection(inspectionCreateDTO);
            createdInspectionId = inspection.getId();

            step("Проверка создания инспекции");
            assertAll("Проверка создания инспекции",
                    () -> assertThat(inspection.getId()).isNotNull(),
                    () -> assertThat(inspection.getSubDivision().getName()).isEqualTo(subDivision),
                    () -> assertThat(inspection.getDateInspection()).isNotNull()
            );

            step("Создание критерия для инспекции");
            InspectionViolationCreateDTO violationCreateDTO = createTestViolationDTO(
                    inspection.getId(),
                    inspection.getSubDivision().getName()
            );
            InspectionViolationDTO violation = createViolation(violationCreateDTO);

            step("Проверка создания критерия");
            assertAll("Проверка критерия",
                    () -> assertThat(violation.getId()).as("ID критерия не должен быть null").isNotNull(),
                    () -> assertThat(violation.getInspectionId())
                            .as("ID инспекции в критерии должно совпадать")
                            .isEqualTo(createdInspectionId));

            step("Создание повторной инспекции");
            InspectionDTO secondaryInspection = createSecondaryInspection(inspection.getId());
            secondaryInspectionId = secondaryInspection.getId();
            InspectionViolationDTO secondaryViolation = getViolations(secondaryInspectionId).get(0);

            step("Проверка повторной инспекции");
            assertAll("Проверка повторной инспекции",
                    () -> assertNotNull(secondaryInspection.getId(), "ID повторной инспекции не должен быть null"),
                    () -> assertEquals(
                            violation.getScore() + 1,
                            secondaryViolation.getScore(),
                            "Балл в повторной инспекции должен быть увеличен на 1"
                    ));

            step("Изменение статуса критерия с 'Не исправлено' на 'Исправлено'");
            String status = changeViolationStatus(secondaryViolation.getId());
            assertEquals(STATUS_FIXED, status, "Статус должен измениться на 'Исправлено'");
            log.debug("Статус критерия {} изменен на: {}", violation.getId(), status);

            step("Проверка невозможности удаления инспекции/критерия с повторной инспекцией");

            step("Попытка удаления критерия оригинальной инспекции (ожидается 403)");
            deleteViolation(violation.getId(), 403);

            step("Попытка удаления оригинальной инспекции (ожидается 403)");
            deleteInspection(inspection.getId(), 403);

            log.info("✓ Удаление оригинальных сущностей заблокировано (403), как и ожидалось");

            step("Удаление критерия повторной инспекции");
            deleteViolation(secondaryViolation.getId(), 204);
            log.info("✓ Критерий повторной инспекции успешно удален");

            log.info("✅ Тест успешно завершен для подразделения: '{}'", subDivision);
        } catch (AssertionError | Exception e) {
            log.error("❌ Тест упал для подразделения: '{}'", subDivision);
            stepFailed(e);
            throw e;
        }
    }

}
