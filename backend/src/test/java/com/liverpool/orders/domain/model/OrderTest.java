package com.liverpool.orders.domain.model;

import com.liverpool.orders.domain.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private static final Customer CUSTOMER = new Customer("u1", "María López", "maria@example.com");
    private static final Address ADDRESS = new Address("Calle 1", "Centro", "CDMX", "CDMX", "06000");

    private Order newOrder() {
        return Order.create("LV-TEST", CUSTOMER, ADDRESS, List.of(
            new OrderItem("P1", 2, new BigDecimal("100.50")),
            new OrderItem("P2", 1, new BigDecimal("49.00"))));
    }

    @Test
    void newOrderStartsAsCreatedAndComputesTotal() {
        Order order = newOrder();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.total()).isEqualByComparingTo("250.00");
    }

    @Test
    void rejectsOrderWithoutItems() {
        assertThatThrownBy(() -> Order.create("LV-TEST", CUSTOMER, ADDRESS, List.of())).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void rejectsInvalidTransition() {
        Order order = newOrder();
        assertThatThrownBy(() -> order.changeStatus(OrderStatus.DELIVERED))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("CREATED");
    }

    @Test
    void followsHappyPathToDelivered() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PAID);
        order.changeStatus(OrderStatus.SHIPPED);
        order.changeStatus(OrderStatus.DELIVERED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void cannotEditAfterPayment() {
        Order order = newOrder();
        order.changeStatus(OrderStatus.PAID);
        assertThatThrownBy(() -> order.updateDetails(CUSTOMER, ADDRESS, List.of(new OrderItem("P3", 1, BigDecimal.TEN)))).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void rejectsItemWithZeroQuantity() {
        assertThatThrownBy(() -> new OrderItem("P1", 0, BigDecimal.ONE)).isInstanceOf(BusinessRuleException.class);
    }
}