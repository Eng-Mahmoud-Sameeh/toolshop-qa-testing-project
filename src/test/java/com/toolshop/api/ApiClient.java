package com.toolshop.api;

import com.toolshop.config.ConfigReader;
import com.toolshop.data.TestDataFactory;
import com.toolshop.data.TestUser;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Talks to the Toolshop REST API directly.
 * UI tests use it to prepare data quickly (e.g. create a new customer)
 * instead of clicking through the Register page every time.
 */
public final class ApiClient {

    private ApiClient() {
    }

    @Step("API: register a new customer")
    public static TestUser registerNewCustomer() {
        TestUser user = TestDataFactory.newCustomer();
        register(user);
        return user;
    }

    @Step("API: register customer {user.email}")
    public static void register(TestUser user) {
        Map<String, Object> address = new LinkedHashMap<>();
        address.put("street", user.street());
        address.put("house_number", user.houseNumber());
        address.put("city", user.city());
        address.put("state", user.state());
        address.put("country", user.country());
        address.put("postal_code", user.postalCode());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("first_name", user.firstName());
        body.put("last_name", user.lastName());
        body.put("dob", user.dob());
        body.put("phone", user.phone());
        body.put("email", user.email());
        body.put("password", user.password());
        body.put("address", address);

        Response response = RestAssured.given()
                .baseUri(ConfigReader.apiUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post("/users/register");

        if (response.statusCode() != 201) {
            throw new IllegalStateException("Could not register test user. Status "
                    + response.statusCode() + ": " + response.asString());
        }
    }
}
