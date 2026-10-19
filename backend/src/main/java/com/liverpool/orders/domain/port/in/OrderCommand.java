package com.liverpool.orders.domain.port.in;

import com.liverpool.orders.domain.model.Address;
import com.liverpool.orders.domain.model.OrderItem;
import java.util.List;

public record OrderCommand(String userId, Address shippingAddress, List<OrderItem> items) {
}