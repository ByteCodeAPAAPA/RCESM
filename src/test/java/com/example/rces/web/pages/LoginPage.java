package com.example.rces.web.pages;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends PageBase {

    private static final String loginField = "#username";
    private static final String loginButton = "#loginButton";

    public LoginPage() {
        $(".login-card").shouldBe(visible);
    }

    @Step("Открыть страницу авторизации")
    public static LoginPage openLoginPage() {
        Selenide.open("login");
        return new LoginPage();
    }

    @Step("Ввести учетные данные: {username}")
    public LoginPage enterCredentials(String username) {
        $(loginField).setValue(username);
        $(loginButton).click();
        return this;
    }

}
