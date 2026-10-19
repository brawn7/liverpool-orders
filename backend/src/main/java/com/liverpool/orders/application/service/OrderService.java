package com.liverpool.orders.application.service;

import com.liverpool.orders.domain.exception.BusinessRuleException;
import com.liverpool.orders.domain.exception.NotFoundException;
import com.liverpool.orders.domain.model.*;
import com.liverpool.orders.domain.port.in.OrderCommand;
import com.liverpool.orders.domain.port.in.OrderUseCase;
import com.liverpool.orders.domain.port.out.OrderRepositoryPort;
import com.liverpool.orders.domain.port.out.UserRepositoryPort;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OrderService implements OrderUseCase {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyMMdd");
    private static final ZoneId ZONE = ZoneId.of("America/Mexico_City");
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OrderRepositoryPort orders;
    private final UserRepositoryPort users;

    public OrderService(OrderRepositoryPort orders, UserRepositoryPort users) {
        this.orders = orders;
        this.users = users;
    }

    @Override
    public Order create(OrderCommand cmd) {
        Customer customer = customerSnapshot(cmd.userId());
        Order order = Order.create(nextOrderNumber(), customer, cmd.shippingAddress(), cmd.items());
        return orders.save(order);
    }

    @Override
    public Order getById(String id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Pedido", id));
    }

    @Override
    public PageResult<Order> search(OrderFilter filter, int page, int size) {
        return orders.search(filter, page, size);
    }

    @Override
    public Order update(String id, OrderCommand cmd) {
        Order order = getById(id);
        order.updateDetails(customerSnapshot(cmd.userId()), cmd.shippingAddress(), cmd.items());
        return orders.save(order);
    }

    @Override
    public Order changeStatus(String id, OrderStatus status) {
        Order order = getById(id);
        order.changeStatus(status);
        return orders.save(order);
    }

    @Override
    public void delete(String id) {
        getById(id);
        orders.deleteById(id);
    }

    private Customer customerSnapshot(String userId) {
        User user = users.findById(userId)
                .orElseThrow(() -> new BusinessRuleException("El usuario '%s' no existe".formatted(userId)));
        return new Customer(user.id(), user.fullName(), user.email());
    }

    private String nextOrderNumber() {
        StringBuilder sb = new StringBuilder("LV-")
                .append(LocalDate.now(ZONE).format(DATE)).append('-');
        for (int i = 0; i < 6; i++) sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        return sb.toString();
    }
}