package com.example.rces.web.pages;

import com.google.gson.Gson;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public abstract class PageBase {

    private final String logoutButton = "#logoutButton";
    private final String menuButton = "#menu-button";
    private final String notificationContainer = "#notifications-container";

    private final String confirmDialogText = "#confirmMessage";
    private final String confirmDialogOkId = "#confirmOk";
    private final String confirmDialogCancelId = "#confirmCancel";

    protected final Gson gson = new Gson();

    @Step("Выйти из учётной записи")
    public void logout() {
        if ($(logoutButton).isDisplayed()) {
            $(logoutButton).click();
        } else {
            performLogout();
        }
    }

    @Step("Открыть главное меню")
    public MainPage openMenu() {
        if ($(menuButton).isDisplayed()) {
            $(menuButton).click();
        } else {
            performOpenMenu();
        }
        return new MainPage();
    }

    @Step("Есть ли уведомление с текстом {notificationText}")
    public PageBase haveNotification(String notificationText) {
        $(notificationContainer).shouldBe(exist);
        $("#notifications-container")
                .$("div.notification")
                .shouldHave(text(notificationText))
                .shouldBe(visible);
        return this;
    }

    @Step("Проверка заполнения обязательных полей")
    public void checkRequiredFields(String form) {
        $(form).shouldNotHave(cssClass(":invalid"));
    }

    @Step("Выполнить выход через URL logout")
    private void performLogout() {
        open("logout");
    }

    @Step("Выполнить переход в меню через URL /menu")
    private void performOpenMenu() {
        open("menu");
    }

    @Step("Найти строку по индексу и выполнить двойной клик")
    public PageBase findRowByIndexAndDoubleClick(String index) {
        $(by("data-index", index)).doubleClick();
        return this;
    }

    @Step("Найти строку по индексу и выполнить правый клик")
    public PageBase findRowByIndexAndRightClick(String index) {
        $(by("data-index", index)).contextClick();
        return this;
    }

    @Step("Сравнение текста в диалоге подтверждения")
    public void checkConfirmDialogText(String text) {
        $(confirmDialogText).shouldBe(visible, Duration.ofMillis(1000)).shouldHave(text(text));
    }

    @Step("Подтвердить действие в диалоге подтверждения")
    public PageBase confirmAction() {
        $(confirmDialogOkId).shouldBe(visible, Duration.ofMillis(1000)).click();
        return this;
    }

    @Step("Отменить действие в диалоге подтверждения")
    public PageBase cancelAction() {
        $(confirmDialogCancelId).shouldBe(visible, Duration.ofMillis(1000)).click();
        return this;
    }

    @Step("Кликнуть на {locator}")
    public PageBase clickOn(String locator) {
        $(locator).click();
        return this;
    }

}
