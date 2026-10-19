package com.liverpool.orders.domain.port.out;

import com.liverpool.orders.domain.model.Order;
import com.liverpool.orders.domain.model.OrderFilter;
import com.liverpool.orders.domain.model.PageResult;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    PageResult<Order> search(OrderFilter filter, int page, int size);
    boolean existsByUserId(String userId);
    void deleteById(String id);
}