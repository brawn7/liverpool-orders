package com.liverpool.orders.infrastructure.adapter.in.web.mapper;

import com.liverpool.orders.domain.model.*;
import com.liverpool.orders.domain.port.in.OrderCommand;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.*;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.OrderResponse.CustomerResponse;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.OrderResponse.OrderItemResponse;

public final class OrderWebMapper {

    private OrderWebMapper() { }

    public static OrderCommand toCommand(OrderRequest r) {
        AddressDto a = r.shippingAddress();
        return new OrderCommand(
            r.userId(),
            new Address(a.street().trim(), a.neighborhood().trim(), a.city().trim(), a.state().trim(), a.zipCode()),
            r.items().stream().map(i -> new OrderItem(i.productCode().trim(), i.quantity(), i.price())).toList());
    }

    public static OrderResponse toResponse(Order o) {
        Customer c = o.getCustomer();
        Address a = o.getShippingAddress();
        return new OrderResponse(
            o.getId(),
            o.getOrderNumber(),
            new CustomerResponse(c.userId(), c.fullName(), c.email()),
            new AddressDto(a.street(), a.neighborhood(), a.city(), a.state(), a.zipCode()),
            o.getItems().stream().map(i -> new OrderItemResponse(i.productCode(), i.quantity(), i.price(), i.subtotal())).toList(),
            o.getStatus(),
            o.total(),
            o.getCreatedAt(),
            o.getUpdatedAt());
    }

    public static OrderSuggestionResponse toSuggestion(Order o) {
        return new OrderSuggestionResponse(o.getId(), o.getOrderNumber(), o.getCustomer().fullName(), o.getCustomer().email(), o.getStatus(), o.total());
    }
}