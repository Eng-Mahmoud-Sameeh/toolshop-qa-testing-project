package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.assertFalse;

@Feature("Access control")
public class AccessTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-ADM-002: a customer cannot open admin pages")
    @Severity(SeverityLevel.BLOCKER)
    public void customerCannotOpenAdminDashboard() {
        loginAsNewCustomer();

        driver().get(com.toolshop.config.ConfigReader.baseUrl() + "/admin/dashboard");
        // Give the app time to redirect or render.
        new WebDriverWait(driver(), Duration.ofSeconds(5))
                .until(d -> ((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState").equals("complete"));
        try {
            Thread.sleep(2000); // the only fixed wait: we are waiting for something NOT to appear
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(driver().getPageSource().contains("Sales over the years"),
                "Admin dashboard content must not be shown to a customer");
    }
}
