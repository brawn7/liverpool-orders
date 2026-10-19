package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record OrderItemRequest(
        @Schema(example = "1098765432")
        @NotBlank(message = "El código de producto es obligatorio")
        @Size(max = 40) String productCode,

        @Schema(example = "2")
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @Max(value = 999, message = "La cantidad no debe exceder 999")
        Integer quantity,

        @Schema(example = "1299.00")
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "El precio admite máximo 2 decimales")
        BigDecimal price) {
}