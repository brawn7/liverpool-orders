package com.liverpool.orders.infrastructure.adapter.out.persistence;

import com.liverpool.orders.domain.model.PageResult;
import com.liverpool.orders.domain.model.User;
import com.liverpool.orders.domain.port.out.UserRepositoryPort;
import com.liverpool.orders.infrastructure.adapter.out.persistence.mapper.UserPersistenceMapper;
import com.liverpool.orders.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        return UserPersistenceMapper.toDomain(repository.save(UserPersistenceMapper.toDocument(user)));
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<User> findAll(int page, int size) {
        Page<User> result = repository
            .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
            .map(UserPersistenceMapper::toDomain);
        return new PageResult<>(result.getContent(), page, size, result.getTotalElements());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}