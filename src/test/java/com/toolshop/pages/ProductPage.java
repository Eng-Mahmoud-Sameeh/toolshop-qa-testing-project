package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

public class ProductPage extends BasePage {

    private static final By NAME = byTest("product-name");
    private static final By PRICE = byTest("unit-price");
    private static final By QUANTITY = byTest("quantity");
    private static final By INCREASE = byTest("increase-quantity");
    private static final By DECREASE = byTest("decrease-quantity");
    private static final By ADD_TO_CART = byTest("add-to-cart");
    private static final By ADD_TO_FAVORITES = byTest("add-to-favorites");
    private static final By OUT_OF_STOCK = By.xpath("//*[normalize-space()='Out of stock']");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public ProductPage waitUntilLoaded() {
        visible(NAME);
        visible(PRICE);
        return this;
    }

    public String name() {
        return text(NAME);
    }

    public BigDecimal price() {
        return new BigDecimal(text(PRICE).replace("$", "").trim());
    }

    public String quantity() {
        return value(QUANTITY);
    }

    @Step("Type quantity '{qty}'")
    public ProductPage typeQuantity(String qty) {
        type(QUANTITY, qty);
        driver.findElement(QUANTITY).sendKeys(Keys.TAB);
        return this;
    }

    @Step("Click +")
    public ProductPage increaseQuantity() {
        click(INCREASE);
        return this;
    }

    @Step("Click -")
    public ProductPage decreaseQuantity() {
        click(DECREASE);
        return this;
    }

    @Step("Add to cart")
    public ProductPage addToCart() {
        Header header = new Header(driver);
        int before = header.cartCount();
        int qty = Integer.parseInt(quantity());
        click(ADD_TO_CART);
        header.waitForCartCount(before + qty);
        return this;
    }

    public boolean isAddToCartEnabled() {
        return visible(ADD_TO_CART).isEnabled();
    }

    public boolean isAddToFavoritesShown() {
        return isDisplayed(ADD_TO_FAVORITES);
    }

    public boolean isOutOfStockShown() {
        return isVisibleWithin(OUT_OF_STOCK, 5);
    }
}
