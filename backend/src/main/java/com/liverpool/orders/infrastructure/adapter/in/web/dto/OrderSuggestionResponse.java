package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import com.liverpool.orders.domain.model.OrderStatus;
import java.math.BigDecimal;

public record OrderSuggestionResponse(String id, String orderNumber, String customerName, String customerEmail, OrderStatus status, BigDecimal total) {
}