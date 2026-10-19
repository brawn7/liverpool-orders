package com.liverpool.orders.infrastructure.adapter.in.web.mapper;

import com.liverpool.orders.domain.model.User;
import com.liverpool.orders.domain.port.in.UserCommand;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.UserRequest;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.UserResponse;

public final class UserWebMapper {

    private UserWebMapper() { }

    public static UserCommand toCommand(UserRequest r) {
        return new UserCommand(r.firstName().trim(), r.paternalLastName().trim(), r.maternalLastName() == null ? null : r.maternalLastName().trim(), r.email());
    }

    public static UserResponse toResponse(User u) {
        return new UserResponse(u.id(), u.firstName(), u.paternalLastName(), u.maternalLastName(), u.fullName(), u.email(), u.createdAt(), u.updatedAt());
    }
}