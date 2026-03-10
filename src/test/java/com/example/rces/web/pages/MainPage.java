package com.example.rces.web.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends PageBase  {

    public enum PageButton {
        SGI("#sgiPageButton"),
        SPE("#spePageButton"),
        ADMIN("#usersPageButton"),
        STTP("#sTTPageButton"),
        REPORT("#reportPageButton"),
        CREATE_BID("#CreatebidPageButton"),
        REQUESTS_OTK("#requestListOtk"),
        EQUIPMENT("#equipmentPageButton"),
        INSPECTION("#inspectionPageButton"),
        LOGGING("#loggingButton"),
        GRAPH("#graphButton");

        private final String selector;

        PageButton(String selector) {
            this.selector = selector;
        }

        public String getSelector() {
            return selector;
        }
    }

    public MainPage() {
        Selenide.open("menu");
        $("#menuContainer").shouldBe(exist, Duration.ofSeconds(5));
    }

    @Step("Проверить доступность кнопки")
    public boolean isPageAvailable(PageButton pageButton) {
        try {
            SelenideElement button = $(pageButton.getSelector());
            return button.exists() &&
                    button.isDisplayed() &&
                    button.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Открыть страницу, если доступна")
    public <T> Optional<T> openPageIfAvailable(PageButton pageButton, Supplier<T> pageSupplier) {
        if (isPageAvailable(pageButton)) {
            $(pageButton.getSelector()).click();
            return Optional.of(pageSupplier.get());
        }
        return Optional.empty();
    }

    @Step("Открыть страницу мероприятий")
    public Optional<SgiPage> openSgiPage() {
        return openPageIfAvailable(PageButton.SGI, SgiPage::new);
    }

    @Step("Открыть страницу перечня УМИ")
    public Optional<SpePage> openSpePage() {
        return openPageIfAvailable(PageButton.SPE, SpePage::new);
    }

    @Step("Открыть страницу администрации")
    public Optional<AdminPage> openAdminPage() {
        return openPageIfAvailable(PageButton.ADMIN, AdminPage::new);
    }

    @Step("Открыть страницу ССЗ")
    public Optional<SttpPage> openSttpPage() {
        return openPageIfAvailable(PageButton.STTP, SttpPage::new);
    }

    @Step("Открыть страницу дерева ЗК")
    public Optional<ReportPage> opeReportPage() {
        return openPageIfAvailable(PageButton.REPORT, ReportPage::new);
    }

    @Step("Открыть страницу создания заявки")
    public Optional<CreateBidPage> openCreateBidPage() {
        return openPageIfAvailable(PageButton.CREATE_BID, CreateBidPage::new);
    }

    @Step("Открыть страницу заявок ОТК")
    public Optional<RequestslistOTK> openRequestslistOTK() {
        return openPageIfAvailable(PageButton.REQUESTS_OTK, RequestslistOTK::new);
    }

    @Step("Открыть страницу оснастки")
    public Optional<EquipmentPage> openEquipmentPage() {
        return openPageIfAvailable(PageButton.EQUIPMENT, EquipmentPage::new);
    }

    @Step("Открыть страницу чек-листов")
    public Optional<InspectionPage> openInspectionPage() {
        return openPageIfAvailable(PageButton.INSPECTION, InspectionPage::new);
    }

    @Step("Открыть страницу логирования")
    public Optional<LoggingPage> openLoggingPage() {
        return openPageIfAvailable(PageButton.LOGGING, LoggingPage::new);
    }

    @Step("Показать график")
    public void showGraph() {
        openPageIfAvailable(PageButton.GRAPH, null);
    }
}
