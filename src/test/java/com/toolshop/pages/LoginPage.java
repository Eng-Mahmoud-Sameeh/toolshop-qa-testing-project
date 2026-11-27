package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By EMAIL = byTest("email");
    private static final By PASSWORD = byTest("password");
    private static final By SUBMIT = byTest("login-submit");
    private static final By LOGIN_ERROR = byTest("login-error");
    private static final By EMAIL_ERROR = byTest("email-error");
    private static final By PASSWORD_ERROR = byTest("password-error");
    private static final By ANY_ERROR = By.cssSelector(
            "[data-test='login-error'], [data-test='email-error'], [data-test='password-error'], .alert-danger");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the Login page")
    public LoginPage open() {
        openPath("/auth/login");
        visible(EMAIL);
        return this;
    }

    @Step("Log in as {email}")
    public void login(String email, String password) {
        type(EMAIL, email);
        type(PASSWORD, password);
        click(SUBMIT);
    }

    @Step("Log in and wait for My account")
    public AccountPage loginSuccessfully(String email, String password) {
        login(email, password);
        waitForUrlContaining("/account");
        return new AccountPage(driver);
    }

    public void submitEmptyForm() {
        click(SUBMIT);
    }

    /** True when any login error (server message or field message) becomes visible. */
    public boolean isAnyErrorShown() {
        return isVisibleWithin(ANY_ERROR, 10);
    }

    public String loginErrorText() {
        return isVisibleWithin(LOGIN_ERROR, 5) ? text(LOGIN_ERROR) : "";
    }

    public String emailErrorText() {
        return text(EMAIL_ERROR);
    }

    public String passwordErrorText() {
        return text(PASSWORD_ERROR);
    }

    public boolean isOnLoginPage() {
        return currentUrl().contains("/auth/login");
    }
}
