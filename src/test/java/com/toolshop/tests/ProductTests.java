package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.pages.ProductPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Product details")
public class ProductTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-PROD-001: product page shows name and price")
    @Severity(SeverityLevel.CRITICAL)
    public void productPageShowsDetails() {
        ProductPage product = homePage().open().findAndOpenProduct("Combination Pliers");

        assertEquals(product.name(), "Combination Pliers");
        assertTrue(product.price().compareTo(BigDecimal.ZERO) > 0, "Price must be positive");
        assertTrue(product.isAddToCartEnabled(), "In-stock product can be added to the cart");
        assertTrue(product.isAddToFavoritesShown(), "Add to favourites should be shown");
    }

    @Test(groups = "regression", description = "TC-PROD-002: out-of-stock product cannot be added to the cart")
    @Severity(SeverityLevel.CRITICAL)
    public void outOfStockProductCannotBeAdded() {
        ProductPage product = homePage().open().findAndOpenProduct("Long Nose Pliers");

        assertTrue(product.isOutOfStockShown(), "'Out of stock' label expected");
        assertFalse(product.isAddToCartEnabled(), "Add to cart must be disabled");
    }

    /** Boundary value analysis on the quantity field (allowed range 1-99). */
    @DataProvider(name = "quantityBoundaries")
    public Object[][] quantityBoundaries() {
        return new Object[][]{
                {"0", "1"},     // just below the lower boundary -> corrected to 1
                {"1", "1"},     // on the lower boundary
                {"2", "2"},     // just above the lower boundary
                {"99", "99"},   // on the upper boundary
                {"100", "99"},  // just above the upper boundary -> corrected to 99
        };
    }

    @Test(dataProvider = "quantityBoundaries", groups = "regression",
            description = "TC-PROD-004/005: quantity field boundaries (BVA)")
    @Severity(SeverityLevel.NORMAL)
    public void quantityStaysWithinLimits(String typed, String expected) {
        ProductPage product = homePage().open().findAndOpenProduct("Combination Pliers");

        product.typeQuantity(typed);

        assertEquals(product.quantity(), expected, "Typed " + typed);
    }

    @Test(groups = "regression", description = "TC-PROD-003: + increases and - never goes below 1")
    @Severity(SeverityLevel.MINOR)
    public void plusAndMinusButtons() {
        ProductPage product = homePage().open().findAndOpenProduct("Combination Pliers");

        product.increaseQuantity();
        assertEquals(product.quantity(), "2");

        product.decreaseQuantity().decreaseQuantity();
        assertEquals(product.quantity(), "1", "Quantity must not go below 1");
    }
}
