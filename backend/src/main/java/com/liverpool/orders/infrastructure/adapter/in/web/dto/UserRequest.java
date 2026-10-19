package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @Schema(example = "Ana")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60, message = "El nombre no debe exceder 60 caracteres")
        String firstName,

        @Schema(example = "Martinez")
        @NotBlank(message = "El apellido paterno es obligatorio")
        @Size(max = 60, message = "El apellido paterno no debe exceder 60 caracteres")
        String paternalLastName,

        @Schema(example = "Hernández")
        @Size(max = 60, message = "El apellido materno no debe exceder 60 caracteres")
        String maternalLastName,

        @Schema(example = "ana.martinez@example.com")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email no debe exceder 120 caracteres")
        String email) {
}