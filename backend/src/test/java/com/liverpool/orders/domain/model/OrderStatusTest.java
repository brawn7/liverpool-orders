package com.liverpool.orders.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @ParameterizedTest(name = "{0} → {1}: permitido = {2}")
    @CsvSource({
        "CREATED,   PAID,      true",
        "CREATED,   CANCELLED, true",
        "CREATED,   SHIPPED,   false",
        "CREATED,   DELIVERED, false",
        "PAID,      SHIPPED,   true",
        "PAID,      CANCELLED, true",
        "PAID,      DELIVERED, false",
        "SHIPPED,   DELIVERED, true",
        "SHIPPED,   CANCELLED, false",
        "DELIVERED, CREATED,   false",
        "CANCELLED, PAID,      false"
    })
    void validatesTransitions(OrderStatus from, OrderStatus to, boolean allowed) {
        assertThat(from.canTransitionTo(to)).isEqualTo(allowed);
    }
}