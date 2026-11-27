package com.toolshop.listeners;

import com.toolshop.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed test (retryCount times, default 1).
 * Why: the demo site is public and sometimes slow or briefly down (risk in the test plan).
 * A test that fails twice is reported as a real failure.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigReader.getInt("retryCount");
        if (attempts < max) {
            attempts++;
            System.out.println("↻ RETRY  " + result.getMethod().getMethodName() + " (attempt " + (attempts + 1) + ")");
            return true;
        }
        return false;
    }
}
