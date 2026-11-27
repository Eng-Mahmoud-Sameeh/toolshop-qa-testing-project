package com.toolshop.data;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Creates fresh, fake test data for every test.
 * A unique email per run means tests never depend on shared demo accounts
 * that other people can lock or change (risk from the test plan).
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static String uniqueSuffix() {
        return System.currentTimeMillis() + "" + ThreadLocalRandom.current().nextInt(100, 999);
    }

    public static String uniqueEmail() {
        return "qa.mahmoud." + uniqueSuffix() + "@example.com";
    }

    /** Meets every password rule: 8+ chars, upper, lower, number, symbol. */
    public static String strongPassword() {
        return "Toolshop#" + ThreadLocalRandom.current().nextInt(1000, 9999) + "Qa";
    }

    /*
     * Address = what the site's own postcode lookup returns for NL 1012AB + 12.
     * The API checks that the city belongs to the country and rejects real cities
     * such as Amsterdam (BUG-012), so tests must use the lookup's values.
     */
    public static TestUser newCustomer() {
        return new TestUser(
                "Mahmoud",
                "Tester",
                "1995-05-15",
                "NL",
                "1012AB",
                "12",
                "Lesingel",
                "Steensel",
                "Noord-Holland",
                "0612345678",
                uniqueEmail(),
                strongPassword());
    }

    public static TestUser withPassword(TestUser user, String password) {
        return new TestUser(user.firstName(), user.lastName(), user.dob(), user.country(), user.postalCode(),
                user.houseNumber(), user.street(), user.city(), user.state(), user.phone(), user.email(), password);
    }
}
