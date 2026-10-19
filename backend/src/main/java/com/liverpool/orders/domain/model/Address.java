package com.liverpool.orders.domain.model;

public record Address(
        String street,
        String neighborhood,
        String city,
        String state,
        String zipCode) {
}