package com.toolshop.tests;

import com.toolshop.api.ApiClient;
import com.toolshop.base.BaseTest;
import com.toolshop.data.CsvDataReader;
import com.toolshop.data.TestDataFactory;
import com.toolshop.data.TestUser;
import com.toolshop.pages.RegisterPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Register")
public class RegisterTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-REG-001: register with valid data, then log in")
    @Severity(SeverityLevel.BLOCKER)
    public void registerWithValidData() {
        TestUser user = TestDataFactory.newCustomer();

        RegisterPage register = new RegisterPage(driver()).open().fillForm(user);
        register.submit();

        assertTrue(register.waitForRedirectToLogin(), "After registering, the Login page should open");
        loginPage().loginSuccessfully(user.email(), user.password());
        assertTrue(header().isLoggedIn(), "The new account should be able to log in");
    }

    @Test(groups = "regression", description = "TC-REG-002: email that already exists is rejected")
    @Severity(SeverityLevel.CRITICAL)
    public void registerWithExistingEmailIsRejected() {
        TestUser existing = ApiClient.registerNewCustomer();

        RegisterPage register = new RegisterPage(driver()).open().fillForm(existing);
        register.submit();

        assertTrue(register.isAnyErrorShown(), "An error should explain the email is taken");
        assertTrue(register.isOnRegisterPage(), "User must stay on the Register page");
    }

    @DataProvider(name = "weakPasswords")
    public Object[][] weakPasswords() {
        return CsvDataReader.read("weak-passwords.csv");
    }

    @Test(dataProvider = "weakPasswords", groups = "regression",
            description = "TC-REG-003/006/007/008: passwords that break a rule are rejected (BVA + decision table)")
    @Severity(SeverityLevel.CRITICAL)
    public void weakPasswordIsRejected(String rule, String password) {
        TestUser user = TestDataFactory.withPassword(TestDataFactory.newCustomer(), password);

        RegisterPage register = new RegisterPage(driver()).open().fillForm(user);
        register.submit();

        assertTrue(register.isPasswordErrorShown(), "Password error expected for rule: " + rule);
        assertTrue(register.isOnRegisterPage(), "No account should be created for rule: " + rule);
    }

    @Test(groups = "regression", description = "TC-REG-004: password with exactly 8 characters is accepted (boundary)")
    @Severity(SeverityLevel.NORMAL)
    public void passwordOnTheBoundaryIsAccepted() {
        // Exactly 8 characters with upper, lower, number and symbol, e.g. "Qa#4821x".
        // Random digits so it is not a well-known password (the site may reject leaked passwords).
        String password = "Qa#" + (1000 + new java.util.Random().nextInt(9000)) + "x";
        assertEquals(password.length(), 8, "Test data must be exactly on the boundary");
        TestUser user = TestDataFactory.withPassword(TestDataFactory.newCustomer(), password);

        RegisterPage register = new RegisterPage(driver()).open().fillForm(user);
        register.submit();

        assertTrue(register.waitForRedirectToLogin(),
                "An 8-character password meeting all rules should be accepted. Errors shown: " + register.allErrorTexts());
    }

    @Test(groups = "regression", description = "TC-REG-009: empty form shows a message for every required field")
    @Severity(SeverityLevel.NORMAL)
    public void emptyFormShowsRequiredMessages() {
        RegisterPage register = new RegisterPage(driver()).open();
        register.submit();

        List<String> errors = register.visibleFieldErrors();
        assertTrue(errors.size() >= 10, "Expected a message for each required field, got: " + errors);
        assertTrue(errors.contains("Email is required"), "Email message missing: " + errors);
    }
}
