package com.liverpool.orders.domain.model;

public record OrderFilter(String term, OrderStatus status) {

    public boolean hasTerm() {
        return term != null && !term.isBlank();
    }
}