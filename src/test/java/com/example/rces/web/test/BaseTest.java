package com.example.rces.web.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Optional;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.example.rces.web.pages.LoginPage.openLoginPage;

public abstract class BaseTest {

    @BeforeAll
    public static void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());

        Configuration.baseUrl = Optional.ofNullable(System.getenv("BASE_URL"))
                .orElse("http://localhost:2520");
        Configuration.browser = System.getenv("BROWSER") != null ?
                System.getenv("BROWSER") : "chrome";
        Configuration.headless = Boolean.parseBoolean(
                System.getenv().getOrDefault("HEADLESS", "true"));
        Configuration.timeout = Long.parseLong(
                System.getenv().getOrDefault("TIMEOUT", "10000"));
        Configuration.pageLoadTimeout = Long.parseLong(
                System.getenv().getOrDefault("PAGE_LOAD_TIMEOUT", "30000"));

        String remoteUrl = System.getenv("SELENIUM_REMOTE_URL");
        if (remoteUrl != null) {
            Configuration.remote = remoteUrl;
            Configuration.browserCapabilities = new ChromeOptions()
                    .addArguments("--no-sandbox", "--disable-dev-shm-usage",
                            "--disable-gpu", "--remote-allow-origins=*");
        }
    }

    @BeforeEach
    public void setUpTest(TestInfo testInfo) {
        clearBrowserCache();
        if (!(this instanceof LoginTest)) {
            openLoginPage().enterCredentials("admin");
        }
    }

    @AfterEach
    public void tearDown() {
        closeWebDriver();
    }

    private void clearBrowserCache() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }
}
