package com.liverpool.orders.domain.model;

import com.liverpool.orders.domain.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Order {

    private final String id;
    private final String orderNumber;
    private Customer customer;
    private Address shippingAddress;
    private List<OrderItem> items;
    private OrderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Order(String id, String orderNumber, Customer customer, Address shippingAddress, List<OrderItem> items, OrderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(String orderNumber, Customer customer, Address shippingAddress, List<OrderItem> items) {
        validateItems(items);
        Instant now = Instant.now();
        return new Order(null, orderNumber, customer, shippingAddress, List.copyOf(items), OrderStatus.CREATED, now, now);
    }

    public static Order restore(String id, String orderNumber, Customer customer, Address shippingAddress, List<OrderItem> items, OrderStatus status, Instant createdAt, Instant updatedAt) {
        return new Order(id, orderNumber, customer, shippingAddress, List.copyOf(items), status, createdAt, updatedAt);
    }

    public void changeStatus(OrderStatus next) {
        if (!status.canTransitionTo(next))
            throw new BusinessRuleException("Transición de estatus inválida: %s → %s".formatted(status, next));
        this.status = next;
        touch();
    }

    public void updateDetails(Customer customer, Address shippingAddress, List<OrderItem> items) {
        if (status != OrderStatus.CREATED)
            throw new BusinessRuleException("Solo se pueden modificar pedidos en estatus CREATED");
        validateItems(items);
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.items = List.copyOf(items);
        touch();
    }

    public BigDecimal total() {
        return items.stream().map(OrderItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static void validateItems(List<OrderItem> items) {
        if (items == null || items.isEmpty())
            throw new BusinessRuleException("El pedido debe tener al menos un producto");
    }

    private void touch() { this.updatedAt = Instant.now(); }

    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public Customer getCustomer() { return customer; }
    public Address getShippingAddress() { return shippingAddress; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}