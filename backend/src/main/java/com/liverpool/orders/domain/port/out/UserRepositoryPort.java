package com.liverpool.orders.domain.port.out;

import com.liverpool.orders.domain.model.PageResult;
import com.liverpool.orders.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    PageResult<User> findAll(int page, int size);
    void deleteById(String id);
}