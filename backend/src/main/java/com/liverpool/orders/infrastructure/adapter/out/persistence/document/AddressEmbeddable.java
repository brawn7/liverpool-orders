package com.liverpool.orders.infrastructure.adapter.out.persistence.document;

public record AddressEmbeddable(String street, String neighborhood, String city, String state, String zipCode) {
}