package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/** "My account" area: overview and the Invoices list. */
public class AccountPage extends BasePage {

    private static final By PAGE_TITLE = byTest("page-title");
    private static final By INVOICE_NUMBERS = By.cssSelector("table tbody tr td:first-child");

    public AccountPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return text(PAGE_TITLE);
    }

    @Step("Open My invoices")
    public AccountPage openInvoices() {
        openPath("/account/invoices");
        return this;
    }

    @Step("Read invoice numbers")
    public List<String> invoiceNumbers() {
        visible(INVOICE_NUMBERS);
        return texts(INVOICE_NUMBERS);
    }
}
