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

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.example.rces.web.pages.LoginPage.openLoginPage;

public abstract class BaseTest {

    @BeforeAll
    public static void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());

        boolean isJenkins = System.getenv("JENKINS_HOME") != null;

        Configuration.baseUrl = firstNonBlank(
                System.getProperty("base.url"),
                System.getenv("BASE_URL"),
                isJenkins ? "http://host.docker.internal:2520" : "http://localhost:2520"
        );

        Configuration.browser = firstNonBlank(
                System.getProperty("browser"),
                System.getenv("BROWSER"),
                "chrome"
        );

        Configuration.headless = Boolean.parseBoolean(firstNonBlank(
                System.getProperty("headless"),
                System.getenv("HEADLESS"),
                String.valueOf(isJenkins)  // В Jenkins всегда headless
        ));

        Configuration.timeout = Long.parseLong(firstNonBlank(
                System.getProperty("timeout"),
                System.getenv("TIMEOUT"),
                "10000"
        ));

        String remoteUrl = firstNonBlank(
                System.getProperty("selenide.remote"),
                System.getenv("SELENIUM_REMOTE_URL"),
                isJenkins ? "http://host.docker.internal:4444/wd/hub" : null
        );

        if (remoteUrl != null) {
            Configuration.remote = remoteUrl;
            Configuration.browserCapabilities = new ChromeOptions()
                    .addArguments("--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*");
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

    private static String firstNonBlank(String... values) {
        return java.util.Arrays.stream(values)
                .filter(v -> v != null && !v.isBlank())
                .findFirst()
                .orElse(null);
    }
}
