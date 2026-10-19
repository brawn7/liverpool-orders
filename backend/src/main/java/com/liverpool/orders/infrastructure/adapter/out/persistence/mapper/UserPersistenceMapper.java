package com.liverpool.orders.infrastructure.adapter.out.persistence.mapper;

import com.liverpool.orders.domain.model.User;
import com.liverpool.orders.infrastructure.adapter.out.persistence.document.UserDocument;

public final class UserPersistenceMapper {

    private UserPersistenceMapper() { }

    public static UserDocument toDocument(User u) {
        return new UserDocument(u.id(), u.firstName(), u.paternalLastName(), u.maternalLastName(), u.email(), u.createdAt(), u.updatedAt());
    }

    public static User toDomain(UserDocument d) {
        return new User(d.getId(), d.getFirstName(), d.getPaternalLastName(), d.getMaternalLastName(), d.getEmail(), d.getCreatedAt(), d.getUpdatedAt());
    }
}