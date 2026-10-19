package com.liverpool.orders.infrastructure.adapter.out.persistence.repository;

import com.liverpool.orders.infrastructure.adapter.out.persistence.document.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringDataOrderRepository extends MongoRepository<OrderDocument, String> {
    boolean existsByCustomerUserId(String userId);
}