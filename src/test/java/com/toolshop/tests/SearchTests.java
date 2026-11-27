package com.toolshop.tests;

import com.toolshop.base.BaseTest;
import com.toolshop.pages.HomePage;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Feature("Search")
public class SearchTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-SRCH-001: search for an existing product")
    @Severity(SeverityLevel.CRITICAL)
    public void searchShowsOnlyMatchingProducts() {
        HomePage home = homePage().open().search("Pliers");

        assertEquals(home.searchCaption(), "Searched for: Pliers");
        List<String> names = home.productNames();
        assertFalse(names.isEmpty(), "Results expected");
        names.forEach(n -> assertTrue(n.toLowerCase().contains("pliers"), "Unexpected result: " + n));
    }

    @Test(groups = "regression", description = "TC-SRCH-002: search with no matches")
    @Severity(SeverityLevel.NORMAL)
    public void searchWithoutMatchesShowsMessage() {
        HomePage home = homePage().open().search("xyz123");

        assertEquals(home.noResultsMessage(), "There are no products found.");
        assertTrue(home.productNames().isEmpty(), "No products should be listed");
    }

    @DataProvider(name = "casing")
    public Object[][] casing() {
        return new Object[][]{{"pliers"}, {"PLIERS"}, {"pLiErS"}};
    }

    @Test(dataProvider = "casing", groups = "regression", description = "TC-SRCH-003: search ignores upper/lower case")
    @Severity(SeverityLevel.MINOR)
    public void searchIsNotCaseSensitive(String term) {
        List<String> expected = homePage().open().search("Pliers").productNames();
        List<String> actual = homePage().open().search(term).productNames();

        assertEquals(actual, expected, "Same results expected for '" + term + "'");
    }

    @Test(groups = "regression", description = "TC-SRCH-004: X button clears the search")
    @Severity(SeverityLevel.MINOR)
    public void clearSearchShowsAllProductsAgain() {
        HomePage home = homePage().open().search("Pliers");
        int filteredCount = home.productNames().size();

        home.clearSearch();

        assertEquals(home.searchBoxValue(), "");
        assertFalse(home.isSearchCaptionShown(), "Search caption should disappear");
        assertTrue(home.productNames().size() > filteredCount, "Full list should be back");
    }
}
