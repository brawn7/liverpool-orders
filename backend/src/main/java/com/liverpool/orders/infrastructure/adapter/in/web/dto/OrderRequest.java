package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
        @Schema(description = "Id del usuario que realiza el pedido")
        @NotBlank(message = "El usuario es obligatorio") String userId,

        @NotNull(message = "La dirección de envío es obligatoria")
        @Valid AddressDto shippingAddress,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        List<@Valid OrderItemRequest> items) {
}