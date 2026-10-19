package com.liverpool.orders.domain.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String resource, String id) {
        super("%s con id '%s' no encontrado".formatted(resource, id));
    }
}