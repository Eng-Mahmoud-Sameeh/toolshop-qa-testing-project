package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** Step 1 of checkout: the shopping cart. */
public class CartPage extends BasePage {

    private static final By ROWS = By.cssSelector("table tbody tr");
    private static final By ROW_TITLE = byTest("product-title");
    private static final By ROW_QUANTITY = byTest("product-quantity");
    private static final By ROW_PRICE = byTest("product-price");
    private static final By ROW_LINE_TOTAL = byTest("line-price");
    private static final By ROW_REMOVE = By.cssSelector(".btn-danger");
    private static final By CART_TOTAL = byTest("cart-total");
    private static final By PROCEED = byTest("proceed-1");

    /** One line of the cart, read from the page. */
    public record CartLine(String product, int quantity, BigDecimal price, BigDecimal lineTotal) {
    }

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the cart")
    public CartPage open() {
        openPath("/checkout");
        visible(CART_TOTAL);
        return this;
    }

    public List<CartLine> lines() {
        visible(CART_TOTAL);
        return driver.findElements(ROWS).stream()
                .filter(row -> !row.findElements(ROW_TITLE).isEmpty())
                .map(row -> new CartLine(
                        row.findElement(ROW_TITLE).getText().trim(),
                        Integer.parseInt(row.findElement(ROW_QUANTITY).getDomProperty("value")),
                        money(row.findElement(ROW_PRICE).getText()),
                        money(row.findElement(ROW_LINE_TOTAL).getText())))
                .toList();
    }

    public CartLine line(String product) {
        return lines().stream()
                .filter(l -> l.product().equals(product))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Product not in cart: " + product));
    }

    public BigDecimal total() {
        return money(text(CART_TOTAL));
    }

    @Step("Change quantity of '{product}' to {quantity}")
    public CartPage changeQuantity(String product, int quantity) {
        BigDecimal totalBefore = total();
        WebElement input = row(product).findElement(ROW_QUANTITY);
        scrollIntoView(input);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(String.valueOf(quantity), Keys.TAB);
        wait.until(d -> total().compareTo(totalBefore) != 0);
        return this;
    }

    @Step("Remove '{product}' from the cart")
    public CartPage remove(String product) {
        int before = lines().size();
        WebElement button = row(product).findElement(ROW_REMOVE);
        scrollIntoView(button);
        button.click();
        wait.until(d -> lines().size() == before - 1);
        return this;
    }

    @Step("Proceed to checkout (step 1)")
    public CheckoutPage proceed() {
        click(PROCEED);
        return new CheckoutPage(driver);
    }

    private WebElement row(String product) {
        return driver.findElements(ROWS).stream()
                .filter(r -> !r.findElements(ROW_TITLE).isEmpty()
                        && r.findElement(ROW_TITLE).getText().trim().equals(product))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Product not in cart: " + product));
    }

    static BigDecimal money(String text) {
        return new BigDecimal(text.replace("$", "").replace(",", "").trim());
    }
}
