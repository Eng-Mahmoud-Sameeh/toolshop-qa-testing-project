package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The navigation bar shown on every page (cart icon, user menu, sign in / sign out). */
public class Header extends BasePage {

    private static final By CART_LINK = byTest("nav-cart");
    private static final By CART_QUANTITY = byTest("cart-quantity");
    private static final By SIGN_IN = byTest("nav-sign-in");
    private static final By USER_MENU = byTest("nav-menu");
    private static final By SIGN_OUT = byTest("nav-sign-out");
    private static final By MENU_TOGGLE = By.cssSelector("button.navbar-toggler");

    public Header(WebDriver driver) {
        super(driver);
    }

    /** Number on the cart icon; 0 when the icon is not shown (empty cart). */
    public int cartCount() {
        if (!isDisplayed(CART_QUANTITY)) {
            return 0;
        }
        String count = text(CART_QUANTITY);
        return count.isEmpty() ? 0 : Integer.parseInt(count);
    }

    @Step("Wait until the cart icon shows {expected}")
    public void waitForCartCount(int expected) {
        wait.until(d -> cartCount() == expected);
    }

    public boolean isLoggedIn() {
        return isVisibleWithin(USER_MENU, 5);
    }

    public String userMenuText() {
        return text(USER_MENU);
    }

    public boolean isSignInLinkShown() {
        return isVisibleWithin(SIGN_IN, 5);
    }

    @Step("Sign out")
    public void signOut() {
        openMenuIfCollapsed();
        click(USER_MENU);
        click(SIGN_OUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGN_IN));
    }

    private void openMenuIfCollapsed() {
        // On narrow windows the menu is hidden behind the "hamburger" button.
        if (!isDisplayed(USER_MENU) && isDisplayed(MENU_TOGGLE)) {
            click(MENU_TOGGLE);
        }
    }
}
