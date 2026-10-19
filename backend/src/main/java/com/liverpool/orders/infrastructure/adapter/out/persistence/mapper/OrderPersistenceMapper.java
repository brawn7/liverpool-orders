package com.liverpool.orders.infrastructure.adapter.out.persistence.mapper;

import com.liverpool.orders.domain.model.*;
import com.liverpool.orders.infrastructure.adapter.out.persistence.document.*;

public final class OrderPersistenceMapper {

    private OrderPersistenceMapper() { }

    public static OrderDocument toDocument(Order o) {
        Customer c = o.getCustomer();
        Address a = o.getShippingAddress();
        return new OrderDocument(
            o.getId(),
            o.getOrderNumber(),
            new CustomerEmbeddable(c.userId(), c.fullName(), c.email()),
            new AddressEmbeddable(a.street(), a.neighborhood(), a.city(), a.state(), a.zipCode()),
            o.getItems().stream()
                .map(i -> new OrderItemEmbeddable(i.productCode(), i.quantity(), i.price()))
                .toList(),
            o.getStatus().name(),
            o.total(),
            o.getCreatedAt(),
            o.getUpdatedAt());
    }

    public static Order toDomain(OrderDocument d) {
        CustomerEmbeddable c = d.getCustomer();
        AddressEmbeddable a = d.getShippingAddress();
        return Order.restore(
            d.getId(),
            d.getOrderNumber(),
            new Customer(c.userId(), c.fullName(), c.email()),
            new Address(a.street(), a.neighborhood(), a.city(), a.state(), a.zipCode()),
            d.getItems().stream()
                .map(i -> new OrderItem(i.productCode(), i.quantity(), i.price()))
                .toList(),
            OrderStatus.valueOf(d.getStatus()),
            d.getCreatedAt(),
            d.getUpdatedAt());
    }
}