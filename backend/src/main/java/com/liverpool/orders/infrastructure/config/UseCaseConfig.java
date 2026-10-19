package com.liverpool.orders.infrastructure.config;

import com.liverpool.orders.application.service.OrderService;
import com.liverpool.orders.application.service.UserService;
import com.liverpool.orders.domain.port.in.OrderUseCase;
import com.liverpool.orders.domain.port.in.UserUseCase;
import com.liverpool.orders.domain.port.out.OrderRepositoryPort;
import com.liverpool.orders.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort users, OrderRepositoryPort orders) {
        return new UserService(users, orders);
    }

    @Bean
    public OrderUseCase orderUseCase(OrderRepositoryPort orders, UserRepositoryPort users) {
        return new OrderService(orders, users);
    }
}