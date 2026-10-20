package com.liverpool.orders.application.service;

import com.liverpool.orders.domain.exception.BusinessRuleException;
import com.liverpool.orders.domain.model.*;
import com.liverpool.orders.domain.port.in.OrderCommand;
import com.liverpool.orders.domain.port.out.OrderRepositoryPort;
import com.liverpool.orders.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepositoryPort orders;
    @Mock UserRepositoryPort users;
    @InjectMocks OrderService service;

    private static final Address ADDRESS = new Address("Calle 1", "Centro", "CDMX", "CDMX", "06000");
    private static final List<OrderItem> ITEMS = List.of(new OrderItem("P1", 1, new BigDecimal("10.00")));

    @Test
    void createBuildsCustomerSnapshotAndFolio() {
        User user = new User("u1", "María", "López", "Hernández", "maria@example.com", Instant.now(), Instant.now());
        when(users.findById("u1")).thenReturn(Optional.of(user));
        when(orders.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order created = service.create(new OrderCommand("u1", ADDRESS, ITEMS));

        assertThat(created.getCustomer().fullName()).isEqualTo("María López Hernández");
        assertThat(created.getCustomer().email()).isEqualTo("maria@example.com");
        assertThat(created.getOrderNumber()).matches("LV-\\d{6}-[A-Z2-9]{6}");
        assertThat(created.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void createFailsWhenUserDoesNotExist() {
        when(users.findById("nope")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(new OrderCommand("nope", ADDRESS, ITEMS))).isInstanceOf(BusinessRuleException.class);
        verify(orders, never()).save(any());
    }

    @Test
    void changeStatusPersistsValidTransition() {
        Order order = Order.restore("o1", "LV-TEST", new Customer("u1", "María", "m@example.com"), ADDRESS, ITEMS, OrderStatus.CREATED, Instant.now(), Instant.now());
        when(orders.findById("o1")).thenReturn(Optional.of(order));
        when(orders.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.changeStatus("o1", OrderStatus.PAID).getStatus()).isEqualTo(OrderStatus.PAID);
    }
}