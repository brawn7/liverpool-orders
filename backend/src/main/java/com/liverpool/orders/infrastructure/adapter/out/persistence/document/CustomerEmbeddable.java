package com.liverpool.orders.infrastructure.adapter.out.persistence.document;

public record CustomerEmbeddable(String userId, String fullName, String email) {
}