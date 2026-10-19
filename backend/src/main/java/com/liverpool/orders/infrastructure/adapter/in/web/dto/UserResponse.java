package com.liverpool.orders.infrastructure.adapter.in.web.dto;

import java.time.Instant;

public record UserResponse(String id, String firstName, String paternalLastName, String maternalLastName, String fullName, String email, Instant createdAt, Instant updatedAt) {
}