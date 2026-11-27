package com.toolshop.base;

import com.toolshop.api.ApiClient;
import com.toolshop.data.TestUser;
import com.toolshop.driver.DriverFactory;
import com.toolshop.pages.CartPage;
import com.toolshop.pages.Header;
import com.toolshop.pages.HomePage;
import com.toolshop.pages.LoginPage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Parent of every test class.
 * Opens a fresh browser before each test and closes it after, so tests never affect each other.
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.createDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser() {
        DriverFactory.quitDriver();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    protected HomePage homePage() {
        return new HomePage(driver());
    }

    protected LoginPage loginPage() {
        return new LoginPage(driver());
    }

    protected CartPage cartPage() {
        return new CartPage(driver());
    }

    protected Header header() {
        return new Header(driver());
    }

    /** Creates a brand-new customer through the API and logs in through the UI. */
    protected TestUser loginAsNewCustomer() {
        TestUser user = ApiClient.registerNewCustomer();
        loginPage().open().loginSuccessfully(user.email(), user.password());
        return user;
    }
}
