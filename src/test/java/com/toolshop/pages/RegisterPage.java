package com.toolshop.pages;

import com.toolshop.data.TestUser;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class RegisterPage extends BasePage {

    private static final By FIRST_NAME = byTest("first-name");
    private static final By LAST_NAME = byTest("last-name");
    private static final By DOB = byTest("dob");
    private static final By COUNTRY = byTest("country");
    private static final By POSTAL_CODE = byTest("postal_code");
    private static final By HOUSE_NUMBER = byTest("house_number");
    private static final By STREET = byTest("street");
    private static final By CITY = byTest("city");
    private static final By STATE = byTest("state");
    private static final By PHONE = byTest("phone");
    private static final By EMAIL = byTest("email");
    private static final By PASSWORD = byTest("password");
    private static final By SUBMIT = byTest("register-submit");
    private static final By PASSWORD_ERROR = byTest("password-error");
    private static final By FIELD_ERRORS = By.cssSelector("[data-test$='-error']");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the Register page")
    public RegisterPage open() {
        openPath("/auth/register");
        visible(FIRST_NAME);
        return this;
    }

    @Step("Fill the registration form for {user.email}")
    public RegisterPage fillForm(TestUser user) {
        type(FIRST_NAME, user.firstName());
        type(LAST_NAME, user.lastName());
        type(DOB, user.dob());
        selectByValue(COUNTRY, user.country());
        type(POSTAL_CODE, user.postalCode());
        type(HOUSE_NUMBER, user.houseNumber());
        // The postcode lookup may fill street/city/state; we type our own values anyway.
        type(STREET, user.street());
        type(CITY, user.city());
        type(STATE, user.state());
        type(PHONE, user.phone());
        type(EMAIL, user.email());
        type(PASSWORD, user.password());
        return this;
    }

    @Step("Click Register")
    public void submit() {
        click(SUBMIT);
    }

    public boolean waitForRedirectToLogin() {
        try {
            waitForUrlContaining("/auth/login");
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public boolean isOnRegisterPage() {
        return currentUrl().contains("/auth/register");
    }

    public boolean isPasswordErrorShown() {
        return isVisibleWithin(PASSWORD_ERROR, 10);
    }

    public boolean isAnyErrorShown() {
        return isVisibleWithin(By.cssSelector("[data-test$='-error'], .alert-danger"), 10);
    }

    /** All error texts currently on the page (field errors and alerts), for clear failure messages. */
    public List<String> allErrorTexts() {
        return texts(By.cssSelector("[data-test$='-error'], .alert-danger")).stream()
                .filter(t -> !t.isEmpty()).toList();
    }

    public List<String> visibleFieldErrors() {
        visible(FIELD_ERRORS);
        return texts(FIELD_ERRORS).stream().filter(t -> !t.isEmpty()).toList();
    }
}
