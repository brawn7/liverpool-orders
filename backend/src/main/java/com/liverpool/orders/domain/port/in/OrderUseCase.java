package com.liverpool.orders.domain.port.in;

import com.liverpool.orders.domain.model.*;

public interface OrderUseCase {
    Order create(OrderCommand command);
    Order getById(String id);
    PageResult<Order> search(OrderFilter filter, int page, int size);
    Order update(String id, OrderCommand command);
    Order changeStatus(String id, OrderStatus status);
    void delete(String id);
}