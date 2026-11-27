package com.toolshop.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.List;

/** Product overview page: search, sort, filters and the product cards. */
public class HomePage extends BasePage {

    private static final By SEARCH_BOX = byTest("search-query");
    private static final By SEARCH_BUTTON = byTest("search-submit");
    private static final By SEARCH_RESET = byTest("search-reset");
    private static final By SEARCH_CAPTION = byTest("search-caption");
    private static final By NO_RESULTS = byTest("no-results");
    private static final By SORT = byTest("sort");
    private static final By PRODUCT_NAMES = byTest("product-name");
    private static final By PRODUCT_PRICES = byTest("product-price");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the home page")
    public HomePage open() {
        openPath("/");
        visible(PRODUCT_NAMES);
        return this;
    }

    @Step("Search for '{term}'")
    public HomePage search(String term) {
        List<String> before = productNames();
        type(SEARCH_BOX, term);
        click(SEARCH_BUTTON);
        visible(SEARCH_CAPTION);
        waitForListToSettle(PRODUCT_NAMES, before);
        return this;
    }

    @Step("Clear the search with the X button")
    public HomePage clearSearch() {
        List<String> before = productNames();
        click(SEARCH_RESET);
        waitForListToSettle(PRODUCT_NAMES, before);
        return this;
    }

    @Step("Sort by '{option}'")
    public HomePage sortBy(String option) {
        List<String> before = productNames();
        selectByText(SORT, option);
        waitForListToSettle(PRODUCT_NAMES, before);
        return this;
    }

    @Step("Tick the filter '{label}'")
    public HomePage tickFilter(String label) {
        List<String> before = productNames();
        click(By.xpath("//label[normalize-space()='" + label + "']/input"));
        waitForListToSettle(PRODUCT_NAMES, before);
        return this;
    }

    @Step("Open product '{name}'")
    public ProductPage openProduct(String name) {
        click(By.xpath("//*[@data-test='product-name'][normalize-space()='" + name + "']"));
        return new ProductPage(driver).waitUntilLoaded();
    }

    /** Uses search to find a product by name (IDs change when the demo site is reset). */
    public ProductPage findAndOpenProduct(String name) {
        return search(name).openProduct(name);
    }

    public List<String> productNames() {
        return texts(PRODUCT_NAMES);
    }

    public List<BigDecimal> productPrices() {
        return texts(PRODUCT_PRICES).stream()
                .map(p -> new BigDecimal(p.replace("$", "").trim()))
                .toList();
    }

    public String searchCaption() {
        return text(SEARCH_CAPTION);
    }

    public boolean isSearchCaptionShown() {
        return isDisplayed(SEARCH_CAPTION);
    }

    public String noResultsMessage() {
        return text(NO_RESULTS);
    }

    public String searchBoxValue() {
        return value(SEARCH_BOX);
    }
}
