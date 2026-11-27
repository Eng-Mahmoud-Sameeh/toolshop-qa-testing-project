package com.toolshop.tests;

import com.toolshop.api.ApiClient;
import com.toolshop.base.BaseTest;
import com.toolshop.data.CsvDataReader;
import com.toolshop.data.TestDataFactory;
import com.toolshop.data.TestUser;
import com.toolshop.pages.LoginPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Login")
public class LoginTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-LOGIN-001: login with valid email and password")
    @Severity(SeverityLevel.BLOCKER)
    public void validLoginOpensMyAccount() {
        TestUser user = ApiClient.registerNewCustomer();

        loginPage().open().loginSuccessfully(user.email(), user.password());

        assertTrue(driver().getCurrentUrl().contains("/account"), "Should be on My account");
        assertTrue(header().userMenuText().contains(user.firstName()), "User name should be in the menu");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return CsvDataReader.read("invalid-logins.csv");
    }

    @Test(dataProvider = "invalidLogins", groups = "regression",
            description = "TC-LOGIN-002/003/005: invalid logins are rejected (data-driven from CSV)")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidLoginIsRejected(String caseName, String email, String password) {
        if (email.equals("EXISTING")) {
            email = ApiClient.registerNewCustomer().email();
        } else if (email.contains("%s")) {
            email = String.format(email, TestDataFactory.uniqueSuffix());
        }

        LoginPage login = loginPage().open();
        login.login(email, password);

        assertTrue(login.isAnyErrorShown(), "An error should be shown for: " + caseName);
        assertTrue(login.isOnLoginPage(), "User must stay on the Login page for: " + caseName);
        assertTrue(header().isSignInLinkShown(), "User must not be logged in for: " + caseName);
    }

    @Test(groups = "regression", description = "TC-LOGIN-004: empty email and password show required messages")
    @Severity(SeverityLevel.NORMAL)
    public void emptyLoginShowsRequiredMessages() {
        LoginPage login = loginPage().open();
        login.submitEmptyForm();

        assertEquals(login.emailErrorText(), "Email is required");
        assertEquals(login.passwordErrorText(), "Password is required");
    }

    @Test(groups = {"smoke", "regression"}, description = "TC-LOGIN-006: sign out")
    @Severity(SeverityLevel.CRITICAL)
    public void signOutLogsTheUserOut() {
        loginAsNewCustomer();

        header().signOut();

        assertTrue(header().isSignInLinkShown(), "Sign in link should be visible again");
    }
}
