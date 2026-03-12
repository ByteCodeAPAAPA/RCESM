package com.example.rces.web.test;

import com.example.rces.dto.SgiCreateDTO;
import com.example.rces.web.pages.MainPage;
import com.example.rces.web.pages.SgiPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.example.rces.data.Sgi.createTestSgiDto;
import static io.qameta.allure.SeverityLevel.CRITICAL;

@Feature("Ui")
@Story("Ui Мероприятия")
@Tags({@Tag("SgiTest"), @Tag("ui")})
@DisplayName("Мероприятия (UI)")
public class SgiTest extends BaseTest {

    private static final String NOTIFICATION_CREATE = "Создано новое мероприятие";
    private static final String NOTIFICATION_EDITING = "Мероприятие успешно отредактировано";
    private static final String NOTIFICATION_EXECUTION = "Факт выполнения сохранен";
    private static final String NOTIFICATION_AGREE = "Мероприятие согласовано";
    private static final String NOTIFICATION_NOT_AGREE = "Согласование отменено";
    private static final String NOTIFICATION_DELETE = "Мероприятие успешно удалено";

    @Test
    @DisplayName("Полный ui цикл работы с мероприятием: создание → редактирование → выполнение → удаление")
    @Owner("ByteCodeAPAA")
    @Severity(CRITICAL)
    void shouldCompleteFullSgiLifecycle() {
        String eventName = "Тестовое мероприятие " + UUID.randomUUID();
        SgiCreateDTO testSgi = createTestSgiDto(eventName);
        LocalDate executionDate = LocalDate.now();
        String executionReport = "Тестовый отчет";

        MainPage mainPage = new MainPage();
        Optional<SgiPage> sgiPage = mainPage.openSgiPage();

        sgiPage.ifPresent((sgi) -> {
            // 1. Создание мероприятия
            sgi.openCreateSgiDialog()
                    .fillCreateDialogSgi(testSgi)
                    .clickOnCreateNewSgiButton()
                    .haveNotification(NOTIFICATION_CREATE);

            String eventIndex = sgi.findIndexSgiByEventName(eventName);

            // 2. Добавление плановой даты
            sgi.openEditSgiDialog(eventIndex)
                    .fillEditingDialogSgiAndSave(LocalDate.now())
                    .haveNotification(NOTIFICATION_EDITING);

            // 3. Добавление факта выполнения
            sgi.openExecutionDialog(eventIndex)
                    .fillExecutionDialog(executionDate, executionReport)
                    .clickOnCreateNewExecutionButton()
                    .haveNotification(NOTIFICATION_EXECUTION);

            // 4. Согласование
            sgi.confirmAction(eventIndex)
                    .haveNotification(NOTIFICATION_AGREE);
            // 5. Отмена согласования
            sgi.confirmAction(eventIndex)
                    .haveNotification(NOTIFICATION_NOT_AGREE);

            // 5. Удаление мероприятия
            sgi.deleteSgiByIndex(eventIndex)
                    .haveNotification(NOTIFICATION_DELETE);
        });
    }

}
