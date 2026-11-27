package com.toolshop.pages;

import com.toolshop.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Parent of every page object. Holds the driver and the shared, safe actions:
 * every action waits for the element first (explicit waits, no Thread.sleep in tests).
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout")));
    }

    /** Builds a locator for the app's data-test attributes, e.g. byTest("login-submit"). */
    protected static By byTest(String value) {
        return By.cssSelector("[data-test='" + value + "']");
    }

    protected void openPath(String path) {
        driver.get(ConfigReader.baseUrl() + path);
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            // Something (chat bubble, toast) covers the element: click it with JavaScript instead.
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        scrollIntoView(element);
        // Select the old value and type over it in one go.
        // (Deleting first would leave the field empty for a moment, and some fields,
        //  like Quantity, immediately refill themselves with "1".)
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(text);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected String value(By locator) {
        return visible(locator).getDomProperty("value");
    }

    protected List<String> texts(By locator) {
        return driver.findElements(locator).stream()
                .map(e -> e.getText().trim())
                .toList();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean isVisibleWithin(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void selectByText(By locator, String visibleText) {
        new Select(visible(locator)).selectByVisibleText(visibleText);
    }

    protected void selectByValue(By locator, String value) {
        new Select(visible(locator)).selectByValue(value);
    }

    protected void waitForUrlContaining(String part) {
        wait.until(ExpectedConditions.urlContains(part));
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /**
     * Waits until a list of texts stops changing (Angular re-renders lists after search/sort/filter).
     * First waits (briefly) for the list to differ from 'before', then until two reads in a row match.
     */
    protected List<String> waitForListToSettle(By locator, List<String> before) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> !Objects.equals(texts(locator), before));
        } catch (TimeoutException ignored) {
            // The list may legitimately stay the same; continue to the stability check.
        }
        AtomicReference<List<String>> last = new AtomicReference<>();
        new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout")), Duration.ofMillis(700))
                .ignoring(StaleElementReferenceException.class)
                .until(d -> {
                    List<String> now = texts(locator);
                    boolean stable = now.equals(last.get());
                    last.set(now);
                    return stable;
                });
        return last.get();
    }
}
