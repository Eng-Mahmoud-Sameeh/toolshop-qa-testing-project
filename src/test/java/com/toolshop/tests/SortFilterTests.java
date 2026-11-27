package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.pages.HomePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Sort and filter")
public class SortFilterTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-SRCH-005: sort by price, low to high")
    @Severity(SeverityLevel.NORMAL)
    public void sortByPriceLowToHigh() {
        List<BigDecimal> prices = homePage().open().sortBy("Price (Low - High)").productPrices();

        assertEquals(prices, prices.stream().sorted().toList(), "Prices should go up");
    }

    @Test(groups = "regression", description = "Sort by price, high to low")
    @Severity(SeverityLevel.NORMAL)
    public void sortByPriceHighToLow() {
        List<BigDecimal> prices = homePage().open().sortBy("Price (High - Low)").productPrices();

        assertEquals(prices, prices.stream().sorted(Comparator.reverseOrder()).toList(), "Prices should go down");
    }

    @Test(groups = "regression", description = "TC-SRCH-006: sort by name, A to Z")
    @Severity(SeverityLevel.MINOR)
    public void sortByNameAToZ() {
        List<String> names = homePage().open().sortBy("Name (A - Z)").productNames();

        assertEquals(names, names.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList(), "Names should be A to Z");
    }

    @Test(groups = "regression", description = "Sort by name, Z to A")
    @Severity(SeverityLevel.MINOR)
    public void sortByNameZToA() {
        List<String> names = homePage().open().sortBy("Name (Z - A)").productNames();

        assertEquals(names, names.stream().sorted(String.CASE_INSENSITIVE_ORDER.reversed()).toList(),
                "Names should be Z to A");
    }

    @Test(groups = "regression", description = "TC-SRCH-007: filter by category Hammer")
    @Severity(SeverityLevel.NORMAL)
    public void filterByCategoryShowsOnlyThatCategory() {
        List<String> names = homePage().open().tickFilter("Hammer").productNames();

        assertFalse(names.isEmpty(), "Hammers expected");
        names.forEach(n -> assertTrue(n.toLowerCase().contains("hammer"), "Not a hammer: " + n));
    }

    @Test(groups = "regression", description = "TC-SRCH-008: category + brand filter + sort together")
    @Severity(SeverityLevel.MINOR)
    public void combineCategoryBrandAndSort() {
        HomePage home = homePage().open()
                .tickFilter("Hammer")
                .tickFilter("ForgeFlex Tools")
                .sortBy("Price (High - Low)");

        List<String> names = home.productNames();
        List<BigDecimal> prices = home.productPrices();
        assertFalse(names.isEmpty(), "Results expected");
        assertFalse(names.contains("Claw Hammer"), "'Claw Hammer' is a MightyCraft product and must be filtered out");
        assertEquals(prices, prices.stream().sorted(Comparator.reverseOrder()).toList(), "Prices should go down");
    }
}
