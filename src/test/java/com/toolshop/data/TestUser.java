package com.toolshop.data;

/**
 * All the data needed to register and log in a customer.
 * A Java "record" is a small class that only holds data.
 */
public record TestUser(
        String firstName,
        String lastName,
        String dob,
        String country,
        String postalCode,
        String houseNumber,
        String street,
        String city,
        String state,
        String phone,
        String email,
        String password) {
}
