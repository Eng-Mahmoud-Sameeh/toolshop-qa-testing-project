package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Steps 2-4 of checkout: sign in / guest, billing address, payment and confirmation. */
public class CheckoutPage extends BasePage {

    private static final By PROCEED_LOGGED_IN = byTest("proceed-2");
    private static final By GUEST_TAB = By.cssSelector("a[href='#guest-tab']");
    private static final By GUEST_EMAIL = byTest("guest-email");
    private static final By GUEST_FIRST_NAME = byTest("guest-first-name");
    private static final By GUEST_LAST_NAME = byTest("guest-last-name");
    private static final By GUEST_SUBMIT = byTest("guest-submit");
    private static final By PROCEED_GUEST = byTest("proceed-2-guest");

    private static final By COUNTRY = byTest("country");
    private static final By POSTAL_CODE = byTest("postal_code");
    private static final By HOUSE_NUMBER = byTest("house_number");
    private static final By STREET = byTest("street");
    private static final By CITY = byTest("city");
    private static final By STATE = byTest("state");
    private static final By PROCEED_BILLING = byTest("proceed-3");

    private static final By PAYMENT_METHOD = byTest("payment-method");
    private static final By FINISH = byTest("finish");
    private static final By PAYMENT_SUCCESS = byTest("payment-success-message");
    private static final By ORDER_CONFIRMATION = By.id("order-confirmation");

    private static final Pattern INVOICE = Pattern.compile("INV-\\d+");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    @Step("Sign-in step: continue as the logged-in customer")
    public CheckoutPage continueAsLoggedInCustomer() {
        click(PROCEED_LOGGED_IN);
        visible(STREET);
        return this;
    }

    @Step("Sign-in step: continue as guest {email}")
    public CheckoutPage continueAsGuest(String email, String firstName, String lastName) {
        click(GUEST_TAB);
        type(GUEST_EMAIL, email);
        type(GUEST_FIRST_NAME, firstName);
        type(GUEST_LAST_NAME, lastName);
        click(GUEST_SUBMIT);
        click(PROCEED_GUEST);
        visible(STREET);
        return this;
    }

    @Step("Fill billing address")
    public CheckoutPage fillBillingAddress(String countryCode, String postalCode, String houseNumber,
                                           String street, String city, String state) {
        selectByValue(COUNTRY, countryCode);
        type(POSTAL_CODE, postalCode);
        type(HOUSE_NUMBER, houseNumber);
        type(STREET, street);
        type(CITY, city);
        type(STATE, state);
        return this;
    }

    /** For a logged-in customer the address comes from the profile; house number may still be empty. */
    @Step("Complete missing billing fields")
    public CheckoutPage ensureHouseNumber(String houseNumber) {
        if (value(HOUSE_NUMBER).isBlank()) {
            type(HOUSE_NUMBER, houseNumber);
        }
        return this;
    }

    public boolean isBillingProceedEnabled() {
        return visible(PROCEED_BILLING).isEnabled();
    }

    @Step("Billing step: proceed to payment")
    public CheckoutPage proceedToPayment() {
        wait.until(d -> d.findElement(PROCEED_BILLING).isEnabled());
        click(PROCEED_BILLING);
        visible(PAYMENT_METHOD);
        return this;
    }

    @Step("Choose payment method '{method}'")
    public CheckoutPage choosePaymentMethod(String method) {
        selectByText(PAYMENT_METHOD, method);
        return this;
    }

    public boolean isFinishEnabled() {
        return visible(FINISH).isEnabled();
    }

    @Step("Click Check payment")
    public String checkPayment() {
        click(FINISH);
        return text(PAYMENT_SUCCESS);
    }

    @Step("Click Confirm")
    public String confirmOrder() {
        click(FINISH);
        return text(ORDER_CONFIRMATION);
    }

    @Step("Check payment, then Confirm")
    public String checkPaymentAndConfirm() {
        checkPayment();
        return confirmOrder();
    }

    public static String invoiceNumberFrom(String confirmation) {
        Matcher m = INVOICE.matcher(confirmation);
        if (!m.find()) {
            throw new AssertionError("No invoice number in: " + confirmation);
        }
        return m.group();
    }
}
