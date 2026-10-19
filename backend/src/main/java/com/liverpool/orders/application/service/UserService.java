package com.liverpool.orders.application.service;

import com.liverpool.orders.domain.exception.ConflictException;
import com.liverpool.orders.domain.exception.NotFoundException;
import com.liverpool.orders.domain.model.PageResult;
import com.liverpool.orders.domain.model.User;
import com.liverpool.orders.domain.port.in.UserCommand;
import com.liverpool.orders.domain.port.in.UserUseCase;
import com.liverpool.orders.domain.port.out.OrderRepositoryPort;
import com.liverpool.orders.domain.port.out.UserRepositoryPort;

public class UserService implements UserUseCase {

    private final UserRepositoryPort users;
    private final OrderRepositoryPort orders;

    public UserService(UserRepositoryPort users, OrderRepositoryPort orders) {
        this.users = users;
        this.orders = orders;
    }

    @Override
    public User create(UserCommand cmd) {
        ensureEmailAvailable(cmd.email(), null);
        return users.save(User.create(cmd.firstName(), cmd.paternalLastName(), cmd.maternalLastName(), cmd.email()));
    }

    @Override
    public User getById(String id) {
        return users.findById(id).orElseThrow(() -> new NotFoundException("Usuario", id));
    }

    @Override
    public PageResult<User> list(int page, int size) {
        return users.findAll(page, size);
    }

    @Override
    public User update(String id, UserCommand cmd) {
        User current = getById(id);
        ensureEmailAvailable(cmd.email(), id);
        return users.save(current.update(cmd.firstName(), cmd.paternalLastName(), cmd.maternalLastName(), cmd.email()));
    }

    @Override
    public void delete(String id) {
        getById(id);
        if (orders.existsByUserId(id))
            throw new ConflictException("No se puede eliminar un usuario con pedidos registrados");
        users.deleteById(id);
    }

    private void ensureEmailAvailable(String email, String currentUserId) {
        users.findByEmail(User.normalizeEmail(email))
                .filter(existing -> !existing.id().equals(currentUserId))
                .ifPresent(existing -> {
                    throw new ConflictException("Ya existe un usuario con el email " + email);
                });
    }
}