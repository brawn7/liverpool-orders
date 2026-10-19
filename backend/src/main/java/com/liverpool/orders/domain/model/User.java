package com.liverpool.orders.domain.model;

import java.time.Instant;

public record User(
        String id,
        String firstName,
        String paternalLastName,
        String maternalLastName,
        String email,
        Instant createdAt,
        Instant updatedAt) {

    public static User create(String firstName, String paternalLastName, String maternalLastName, String email) {
        Instant now = Instant.now();
        return new User(null, firstName, paternalLastName, maternalLastName, email.toLowerCase().trim(), now, now);
    }

    public User update(String firstName, String paternalLastName, String maternalLastName, String email) {
        return new User(id, firstName, paternalLastName, maternalLastName, email.toLowerCase().trim(), createdAt, Instant.now());
    }

    public String fullName() {
        return String.join(" ", firstName, paternalLastName, maternalLastName == null ? "" : maternalLastName).trim();
    }
}