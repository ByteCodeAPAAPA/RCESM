package com.example.rces.web.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.rces.dto.SgiCreateDTO;
import io.qameta.allure.Step;

import java.time.Duration;
import java.time.LocalDate;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.example.rces.utils.DateUtil.formatedDate;

public class SgiPage extends PageBase {

    private final String createDialogButton = "#create-button";
    private final String filterDialogButton = "#filter-button";
    private final String editingNameDialogButton = "editingButton";
    private final String executionNameDialogButton = "executionButton";
    private final String createNewSgiButton = "#createBtn";
    private final String saveEditingButton = "#saveEditBtn";
    private final String saveFactExecutionButton = "#saveBtn";
    private final String deleteSgiButton = "#deleteSgiButton";
    private final String toggleAgreementCheckBox = "#toggleAgreement";
    private final String printSgiButton = "#printSgiButton";

    public SgiPage() {
        $("#paginationContainer").shouldBe(exist, Duration.ofSeconds(5));
    }

    @Step("Открыть диалог создания мероприятия")
    public SgiPage openCreateSgiDialog() {
        $(createDialogButton).click();
        return this;
    }

    @Step("Открыть диалог редактирования мероприятия")
    public SgiPage openEditSgiDialog(String index) {
        SelenideElement row = $(by("data-index", index));
        row.find(String.format("[name='%s']", editingNameDialogButton)).click();
        return this;
    }

    @Step("Ввод планового срока и сохранение изменений")
    public SgiPage fillEditingDialogSgiAndSave(LocalDate planDate) {
        $("#editing-dialog").$(by("data-field", "planDate"))
                .setValue(formatedDate(planDate));
        $(saveEditingButton).click();
        return this;
    }

    @Step("Открыть диалог факта выполнения строки с индексом {index}")
    public SgiPage openExecutionDialog(String index) {
        SelenideElement row = $(by("data-index", index));
        row.find(String.format("[name='%s']", executionNameDialogButton)).click();
        return this;
    }

    @Step("Заполнение данных создания мероприятия")
    public SgiPage fillCreateDialogSgi(SgiCreateDTO sgi) {
        $("[name='workcenter']").setValue(sgi.getWorkcenter());
        $("[name='event']").setValue(sgi.getEvent());
        $("[name='actions']").setValue(sgi.getActions());
        $("[name='department']").selectOptionByValue("builder");

        Selenide.executeJavaScript(
                "arguments[0].value = arguments[1]",
                $("[name='employee']"),
                sgi.getEmployee().getName()
        );
        Selenide.executeJavaScript(
                "arguments[0].value = arguments[1]",
                $("[name='hiddenEmployee']"),
                gson.toJson(sgi.getEmployee())
        );
        $("[name='desiredDate']").setValue(formatedDate(sgi.getDesiredDate()));
        $("[name='note']").setValue(sgi.getNote());
        return this;
    }

    @Step("Заполнение данных факта выполнения")
    public SgiPage fillExecutionDialog(LocalDate executionDate, String report) {
        $(by("data-field", "executionDate")).setValue(formatedDate(executionDate));
        $(by("data-field", "report")).setValue(report);
        return this;
    }

    @Step("Проверка обязательных полей и нажать на кнопку создания мероприятие")
    public SgiPage clickOnCreateNewSgiButton() {
        $(createNewSgiButton).click();
        checkRequiredFields("#sgiForm");
        return this;
    }

    @Step("Нажать на кнопку сохранить факт выполнения мероприятия")
    public SgiPage clickOnCreateNewExecutionButton() {
        $(saveFactExecutionButton).click();
        return this;
    }

    @Step("Согласование/Отмена согласования мероприятия")
    public SgiPage confirmAction(String index) {
        SelenideElement row = $(by("data-index", index));
        row.$(toggleAgreementCheckBox).click();
        return this;
    }

    @Step("Поиск индекса строки по названию мероприятия")
    public String findIndexSgiByEventName(String event) {
        SelenideElement row = $$(by("data-field", "event")).findBy(text(event));
        return row.parent() // div.row-items-row
                .parent() // div.row-items
                .getAttribute("data-index");
    }

    @Step("Удаление мероприятия по индексу строки")
    public SgiPage deleteSgiByIndex(String index) {
        findRowByIndexAndDoubleClick(index)
                .findRowByIndexAndRightClick(index)
                .clickOn(deleteSgiButton)
                .confirmAction();
        return this;
    }
}
