package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.data.TestDataFactory;
import com.toolshop.data.TestUser;
import com.toolshop.pages.AccountPage;
import com.toolshop.pages.CheckoutPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Checkout")
public class CheckoutTests extends BaseTest {

    @Test(groups = {"smoke", "regression"},
            description = "TC-CHK-001 + TC-INV-001: customer checkout with Cash on Delivery; invoice appears in My invoices")
    @Severity(SeverityLevel.BLOCKER)
    public void customerCheckoutCreatesInvoice() {
        TestUser user = loginAsNewCustomer();
        homePage().open().findAndOpenProduct("Combination Pliers").addToCart();

        CheckoutPage checkout = cartPage().open().proceed()
                .continueAsLoggedInCustomer()
                .ensureHouseNumber(user.houseNumber())
                .proceedToPayment()
                .choosePaymentMethod("Cash on Delivery");

        assertEquals(checkout.checkPayment(), "Payment was successful");
        String confirmation = checkout.confirmOrder();
        assertTrue(confirmation.startsWith("Thanks for your order!"), confirmation);

        String invoice = CheckoutPage.invoiceNumberFrom(confirmation);
        assertTrue(new AccountPage(driver()).openInvoices().invoiceNumbers().contains(invoice),
                "Invoice " + invoice + " should be listed in My invoices");
        assertEquals(header().cartCount(), 0, "Cart should be empty after ordering");
    }

    @Test(groups = {"smoke", "regression"}, description = "TC-CHK-002: guest checkout with Cash on Delivery")
    @Severity(SeverityLevel.CRITICAL)
    public void guestCheckout() {
        homePage().open().findAndOpenProduct("Combination Pliers").addToCart();

        String confirmation = cartPage().open().proceed()
                .continueAsGuest(TestDataFactory.uniqueEmail(), "Guest", "Tester")
                .fillBillingAddress("NL", "1012AB", "12", "Lesingel", "Steensel", "Noord-Holland")
                .proceedToPayment()
                .choosePaymentMethod("Cash on Delivery")
                .checkPaymentAndConfirm();

        assertTrue(confirmation.matches("Thanks for your order! Your invoice number is INV-\\d+\\.?"), confirmation);
    }

    @Test(groups = "regression", description = "TC-CHK-003 + TC-CHK-004: required billing fields and payment method")
    @Severity(SeverityLevel.NORMAL)
    public void checkoutBlocksMissingData() {
        homePage().open().findAndOpenProduct("Combination Pliers").addToCart();

        CheckoutPage checkout = cartPage().open().proceed()
                .continueAsGuest(TestDataFactory.uniqueEmail(), "Guest", "Tester");
        assertFalse(checkout.isBillingProceedEnabled(), "Cannot continue with an empty billing address");

        checkout.fillBillingAddress("NL", "1012AB", "12", "Lesingel", "Steensel", "Noord-Holland")
                .proceedToPayment();
        assertFalse(checkout.isFinishEnabled(), "'Check payment' must be disabled until a method is chosen");
    }
}
