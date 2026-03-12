package com.example.rces.api.test;

import com.example.rces.dto.InspectionCreateDTO;
import com.example.rces.dto.InspectionDTO;
import com.example.rces.dto.InspectionViolationCreateDTO;
import com.example.rces.dto.InspectionViolationDTO;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static com.example.rces.api.steps.InspectionControllerSteps.*;
import static com.example.rces.api.steps.SgiControllerSteps.deleteSgiById;
import static com.example.rces.data.Inspection.createTestInspectionDTO;
import static com.example.rces.data.Inspection.createTestViolationDTO;
import static io.qameta.allure.SeverityLevel.CRITICAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Feature("Api")
@Story("Api Инспекции")
@Tags({@Tag("InspectionController"), @Tag("api")})
@DisplayName("Инспекции (API)")
public class InspectionControllerTest extends BaseApiTest {

    private Integer createdInspectionId;
    private Integer secondaryInspectionId;

    @AfterEach
    void cleanUp() {
        if (secondaryInspectionId!=null) {
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
    @MethodSource("randomSubDivision")
    public void fullInspectionLifecycle(String subDivision) {
        InspectionCreateDTO inspectionCreateDTO = createTestInspectionDTO(subDivision);

        // 1. Создание инспекции
        InspectionDTO inspection = createInspection(inspectionCreateDTO);
        createdInspectionId = inspection.getId();
        assertAll("Проверка создания",
                () -> assertThat(inspection.getId()).isNotNull(),
                () -> assertThat(inspection.getDateInspection()).isNotNull());

        // 2. Создание критерия
        InspectionViolationCreateDTO violationCreateDTO = createTestViolationDTO(inspection.getId(), inspection.getSubDivision().getName());
        InspectionViolationDTO violation = createViolation(violationCreateDTO);
        assertAll("Проверка критерия",
                () -> assertThat(violation.getId()).isNotNull(),
                () -> assertThat(violation.getInspectionId()).isEqualTo(createdInspectionId));

        //3. Изменения статуса критерия с "Не исправлено" на "Исправлено"
        String status = changeViolationStatus(violation.getId());
        assertEquals("Исправлено", status);
//        //3. Изменения статуса критерия с"Исправлено" на "Не исправлено"
//        status = changeViolationStatus(violation.getId());
//        assertEquals("Не исправлено", status);

        //4. Создание повторной инспекции
        InspectionDTO secondaryInspection = createSecondaryInspection(inspection.getId());
        secondaryInspectionId = secondaryInspection.getId();
        InspectionViolationDTO secondaryViolation = secondaryInspection.getViolation().get(0);
        assertAll("Проверка повторной инспекции",
                () -> assertNotNull(secondaryInspection.getId()),
                () -> assertEquals(violation.getScore() + 1, secondaryViolation.getScore()));

        //5. Проверка выпадения exception при удалении инспекции или критерия инспекции у которой есть повторная инспекция
        deleteViolation(violation.getId(), 403);
        deleteInspection(inspection.getId(), 403);

        //6. Удаление критерия у повторной инспекции
        deleteViolation(secondaryViolation.getId(), 204);
    }

    static Stream<Arguments> randomSubDivision() {
        Faker faker = new Faker();
        List<String> subDivisions = Arrays.asList(
                "WorkShop1", "WorkShop3", "WorkShop4", "WorkShop5", "WorkShop6", "WorkShop8", "YTO", "OPiO");
        return Stream.of(Arguments.of(
                subDivisions.get(ThreadLocalRandom.current().nextInt(subDivisions.size())),
                faker.lorem().sentence()
        ));
    }


}
