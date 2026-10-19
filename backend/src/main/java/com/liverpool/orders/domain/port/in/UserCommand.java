package com.liverpool.orders.domain.port.in;

public record UserCommand(String firstName, String paternalLastName, String maternalLastName, String email) {
}