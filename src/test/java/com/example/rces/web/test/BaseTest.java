package com.example.rces.web.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.example.rces.web.pages.LoginPage.openLoginPage;

public abstract class BaseTest {

    @BeforeAll
    public static void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());

        // Читаем переменные (приоритет: системные свойства > переменные окружения > дефолт)
        Configuration.baseUrl = getConfigValue("BASE_URL", "http://localhost:2520");
        Configuration.browser = getConfigValue("BROWSER", "chrome");
        Configuration.headless = Boolean.parseBoolean(getConfigValue("HEADLESS", "false"));
        Configuration.timeout = Long.parseLong(getConfigValue("TIMEOUT", "10000"));

        String remoteUrl = getConfigValue("SELENIUM_REMOTE_URL", null);
        if (remoteUrl != null && !remoteUrl.isEmpty()) {
            Configuration.remote = remoteUrl;
            Configuration.browserCapabilities = createChromeOptions();
        }

        // Дополнительные настройки
        Configuration.pageLoadTimeout = Long.parseLong(getConfigValue("PAGE_LOAD_TIMEOUT", "30000"));
        Configuration.browserSize = getConfigValue("BROWSER_SIZE", "1920x1080");
    }

    private static String getConfigValue(String key, String defaultValue) {
        // 1. Проверяем системные свойства (-Dkey=value)
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.isEmpty()) {
            return systemProp;
        }

        // 2. Проверяем переменные окружения
        String envVar = System.getenv(key);
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        }

        // 3. Используем значение по умолчанию
        return defaultValue;
    }

    private static MutableCapabilities createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-allow-origins=*",
                "--window-size=1920,1080"
        );

        // Важно для Selenium Grid
        options.setCapability("se:name", "RCES UI Tests");
        options.setCapability("se:recordVideo", false);

        return options;
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
