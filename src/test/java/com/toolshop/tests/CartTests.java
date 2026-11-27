package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.pages.CartPage;
import com.toolshop.pages.CartPage.CartLine;
import com.toolshop.pages.ProductPage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;

@Feature("Cart")
public class CartTests extends BaseTest {

    private static final String PLIERS = "Combination Pliers";
    private static final String HAMMER = "Hammer";

    private BigDecimal addProduct(String name) {
        ProductPage product = homePage().open().findAndOpenProduct(name);
        BigDecimal price = product.price();
        product.addToCart();
        return price;
    }

    @Test(groups = {"smoke", "regression"}, description = "TC-CART-001: add one product to an empty cart")
    @Severity(SeverityLevel.BLOCKER)
    public void addOneProduct() {
        BigDecimal price = addProduct(PLIERS);

        assertEquals(header().cartCount(), 1);
        CartPage cart = cartPage().open();
        CartLine line = cart.line(PLIERS);
        assertEquals(line.quantity(), 1);
        assertEquals(line.lineTotal(), price);
        assertEquals(cart.total(), price);
    }

    @Test(groups = "regression", description = "TC-CART-002: adding the same product twice gives one line with quantity 2")
    @Severity(SeverityLevel.CRITICAL)
    public void addSameProductTwice() {
        BigDecimal price = addProduct(PLIERS);
        addProduct(PLIERS);

        CartPage cart = cartPage().open();
        assertEquals(cart.lines().size(), 1, "Only one line expected");
        assertEquals(cart.line(PLIERS).quantity(), 2);
        assertEquals(cart.total(), price.multiply(BigDecimal.valueOf(2)));
    }

    @Test(groups = "regression", description = "TC-CART-003: total of two different products")
    @Severity(SeverityLevel.CRITICAL)
    public void totalOfTwoProducts() {
        BigDecimal pliers = addProduct(PLIERS);
        BigDecimal hammer = addProduct(HAMMER);

        CartPage cart = cartPage().open();
        assertEquals(cart.lines().size(), 2);
        assertEquals(cart.total(), pliers.add(hammer));
    }

    @Test(groups = "regression", description = "TC-CART-004: changing quantity updates line and cart total")
    @Severity(SeverityLevel.CRITICAL)
    public void changeQuantityUpdatesTotals() {
        BigDecimal price = addProduct(PLIERS);

        CartPage cart = cartPage().open().changeQuantity(PLIERS, 3);

        BigDecimal expected = price.multiply(BigDecimal.valueOf(3));
        assertEquals(cart.line(PLIERS).quantity(), 3);
        assertEquals(cart.line(PLIERS).lineTotal(), expected);
        assertEquals(cart.total(), expected);
    }

    @Test(groups = "regression", description = "TC-CART-005: remove a product from the cart")
    @Severity(SeverityLevel.NORMAL)
    public void removeProduct() {
        BigDecimal pliers = addProduct(PLIERS);
        addProduct(HAMMER);

        CartPage cart = cartPage().open().remove(HAMMER);

        assertEquals(cart.lines().size(), 1);
        assertEquals(cart.total(), pliers);
    }
}
