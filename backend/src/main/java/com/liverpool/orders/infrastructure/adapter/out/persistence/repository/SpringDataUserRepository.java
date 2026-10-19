package com.liverpool.orders.infrastructure.adapter.out.persistence.repository;

import com.liverpool.orders.infrastructure.adapter.out.persistence.document.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SpringDataUserRepository extends MongoRepository<UserDocument, String> {
    Optional<UserDocument> findByEmail(String email);
}