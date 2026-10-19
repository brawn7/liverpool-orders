package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import com.liverpool.orders.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String id,
        String orderNumber,
        CustomerResponse customer,
        AddressDto shippingAddress,
        List<OrderItemResponse> items,
        OrderStatus status,
        BigDecimal total,
        Instant createdAt,
        Instant updatedAt) {

    public record CustomerResponse(String userId, String fullName, String email) { }

    public record OrderItemResponse(String productCode, int quantity, BigDecimal price, BigDecimal subtotal) { }
}