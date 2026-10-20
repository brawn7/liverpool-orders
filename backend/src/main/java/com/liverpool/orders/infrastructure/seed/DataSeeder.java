package com.liverpool.orders.infrastructure.seed;

import com.liverpool.orders.domain.model.*;
import com.liverpool.orders.domain.port.in.OrderCommand;
import com.liverpool.orders.domain.port.in.OrderUseCase;
import com.liverpool.orders.domain.port.in.UserCommand;
import com.liverpool.orders.domain.port.in.UserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final int ORDER_COUNT = 30;
    private static final long RANDOM_SEED = 42L;

    private record Product(String code, BigDecimal price) { }

    private static final List<UserCommand> USERS = List.of(
        new UserCommand("María", "López", "Hernández", "maria.lopez@example.com"),
        new UserCommand("José", "Martínez", "García", "jose.martinez@example.com"),
        new UserCommand("Ana Sofía", "Ramírez", "Torres", "ana.ramirez@example.com"),
        new UserCommand("Luis Fernando", "Gómez", "Díaz", "luis.gomez@example.com"),
        new UserCommand("Daniela", "Cruz", "Morales", "daniela.cruz@example.com"),
        new UserCommand("Carlos Alberto", "Reyes", "Ortiz", "carlos.reyes@example.com"),
        new UserCommand("Fernanda", "Jiménez", "Vargas", "fernanda.jimenez@example.com"),
        new UserCommand("Ricardo", "Mendoza", null, "ricardo.mendoza@example.com"));

    private static final List<Address> ADDRESSES = List.of(
        new Address("Av. Insurgentes Sur 1234", "Del Valle", "Ciudad de México", "CDMX", "03100"),
        new Address("Calle Durango 45", "Roma Norte", "Ciudad de México", "CDMX", "06700"),
        new Address("Av. Vallarta 3020", "Vallarta Poniente", "Guadalajara", "Jalisco", "44110"),
        new Address("Av. Constitución 800", "Centro", "Monterrey", "Nuevo León", "64000"),
        new Address("Blvd. Kukulcán Km 12", "Zona Hotelera", "Cancún", "Quintana Roo", "77500"),
        new Address("Calle 5 de Mayo 210", "Centro Histórico", "Puebla", "Puebla", "72000"),
        new Address("Av. Universidad 1500", "Santa Cruz Atoyac", "Ciudad de México", "CDMX", "03310"),
        new Address("Paseo de la Reforma 505", "Cuauhtémoc", "Ciudad de México", "CDMX", "06500"));

    private static final List<Product> PRODUCTS = List.of(
        new Product("1098765432", new BigDecimal("1299.00")),
        new Product("1012345678", new BigDecimal("349.50")),
        new Product("1087654321", new BigDecimal("4599.00")),
        new Product("1123456789", new BigDecimal("899.90")),
        new Product("1056781234", new BigDecimal("12999.00")),
        new Product("1099887766", new BigDecimal("259.00")),
        new Product("1077665544", new BigDecimal("2149.00")),
        new Product("1045678912", new BigDecimal("679.00")));

    private static final OrderStatus[] TARGETS = {
        OrderStatus.CREATED, OrderStatus.PAID, OrderStatus.SHIPPED,
        OrderStatus.DELIVERED, OrderStatus.DELIVERED, OrderStatus.CANCELLED };

    private final UserUseCase userUseCase;
    private final OrderUseCase orderUseCase;

    public DataSeeder(UserUseCase userUseCase, OrderUseCase orderUseCase) {
        this.userUseCase = userUseCase;
        this.orderUseCase = orderUseCase;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userUseCase.list(0, 1).totalElements() > 0) {
            log.info("Seed omitido: la base ya contiene datos");
            return;
        }

        Random random = new Random(RANDOM_SEED);
        List<User> users = USERS.stream().map(userUseCase::create).toList();

        for (int i = 0; i < ORDER_COUNT; i++) {
            int userIndex = random.nextInt(users.size());
            Order order = orderUseCase.create(new OrderCommand(users.get(userIndex).id(), ADDRESSES.get(userIndex), randomItems(random)));

            for (OrderStatus step : pathTo(TARGETS[i % TARGETS.length])) {
                orderUseCase.changeStatus(order.getId(), step);
            }
        }
        log.info("Seed completado: {} usuarios, {} pedidos", users.size(), ORDER_COUNT);
    }

    private static List<OrderItem> randomItems(Random random) {
        List<Product> shuffled = new ArrayList<>(PRODUCTS);
        Collections.shuffle(shuffled, random);
        int count = 1 + random.nextInt(3);
        return shuffled.subList(0, count).stream()
            .map(p -> new OrderItem(p.code(), 1 + random.nextInt(3), p.price()))
            .toList();
    }

    private static List<OrderStatus> pathTo(OrderStatus target) {
        return switch (target) {
            case CREATED -> List.of();
            case PAID -> List.of(OrderStatus.PAID);
            case SHIPPED -> List.of(OrderStatus.PAID, OrderStatus.SHIPPED);
            case DELIVERED -> List.of(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.DELIVERED);
            case CANCELLED -> List.of(OrderStatus.CANCELLED);
        };
    }
}