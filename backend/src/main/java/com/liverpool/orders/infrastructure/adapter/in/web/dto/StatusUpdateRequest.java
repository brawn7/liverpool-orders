package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import com.liverpool.orders.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "El estatus es obligatorio") OrderStatus status) {
}