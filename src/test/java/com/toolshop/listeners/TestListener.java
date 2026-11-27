package com.toolshop.listeners;

import com.toolshop.driver.DriverFactory;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Runs automatically around every test (registered in testng.xml).
 * On failure: takes a screenshot, attaches it to the Allure report and saves it to target/screenshots.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("▶ START  " + name(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("✔ PASS   " + name(result));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("↷ SKIP   " + name(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("✘ FAIL   " + name(result) + " -> " + result.getThrowable());
        if (!DriverFactory.hasDriver()) {
            return;
        }
        try {
            // Print what the browser actually shows, so CI logs explain the failure without a screenshot.
            org.openqa.selenium.WebDriver d = DriverFactory.getDriver();
            String bodyText = d.findElement(org.openqa.selenium.By.tagName("body")).getText().replaceAll("\\s+", " ");
            System.out.println("    page title: " + d.getTitle());
            System.out.println("    page url  : " + d.getCurrentUrl());
            System.out.println("    page text : " + bodyText.substring(0, Math.min(300, bodyText.length())));

            byte[] png = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png), "png");
            Allure.addAttachment("URL on failure", DriverFactory.getDriver().getCurrentUrl());

            Path folder = Paths.get("target", "screenshots");
            Files.createDirectories(folder);
            Files.write(folder.resolve(name(result).replaceAll("[^a-zA-Z0-9._-]", "_") + ".png"), png);
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not take screenshot: " + e.getMessage());
        }
    }

    private static String name(ITestResult result) {
        String base = result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        return params.length == 0 ? base : base + java.util.Arrays.toString(params);
    }
}
