package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @Schema(example = "Av. Insurgentes Sur 1234")
        @NotBlank(message = "La calle es obligatoria") @Size(max = 120) String street,

        @Schema(example = "Del Valle")
        @NotBlank(message = "La colonia es obligatoria") @Size(max = 80) String neighborhood,

        @Schema(example = "Ciudad de México")
        @NotBlank(message = "La ciudad es obligatoria") @Size(max = 80) String city,

        @Schema(example = "CDMX")
        @NotBlank(message = "El estado es obligatorio") @Size(max = 60) String state,

        @Schema(example = "03100")
        @NotBlank(message = "El código postal es obligatorio")
        @Pattern(regexp = "\\d{5}", message = "El código postal debe tener 5 dígitos")
        String zipCode) {
}