package com.liverpool.orders.domain.port.in;

import com.liverpool.orders.domain.model.PageResult;
import com.liverpool.orders.domain.model.User;

public interface UserUseCase {
    User create(UserCommand command);
    User getById(String id);
    PageResult<User> list(int page, int size);
    User update(String id, UserCommand command);
    void delete(String id);
}