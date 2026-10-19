package com.liverpool.orders.infrastructure.adapter.out.persistence;

import com.liverpool.orders.domain.model.Order;
import com.liverpool.orders.domain.model.OrderFilter;
import com.liverpool.orders.domain.model.PageResult;
import com.liverpool.orders.domain.port.out.OrderRepositoryPort;
import com.liverpool.orders.infrastructure.adapter.out.persistence.document.OrderDocument;
import com.liverpool.orders.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import com.liverpool.orders.infrastructure.adapter.out.persistence.repository.SpringDataOrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;
    private final MongoTemplate mongoTemplate;

    public OrderPersistenceAdapter(SpringDataOrderRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Order save(Order order) {
        return OrderPersistenceMapper.toDomain(repository.save(OrderPersistenceMapper.toDocument(order)));
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(id).map(OrderPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Order> search(OrderFilter filter, int page, int size) {
        Query query = buildQuery(filter);
        long total = mongoTemplate.count(query, OrderDocument.class);

        query.with(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<Order> content = mongoTemplate.find(query, OrderDocument.class).stream()
            .map(OrderPersistenceMapper::toDomain)
            .toList();

        return new PageResult<>(content, page, size, total);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return repository.existsByCustomerUserId(userId);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Query buildQuery(OrderFilter filter) {
        List<Criteria> criteria = new ArrayList<>();

        if (filter.status() != null) {
            criteria.add(Criteria.where("status").is(filter.status().name()));
        }
        if (filter.hasTerm()) {
            Pattern pattern = Pattern.compile(Pattern.quote(filter.term().trim()), Pattern.CASE_INSENSITIVE);
            criteria.add(new Criteria().orOperator(
                Criteria.where("orderNumber").regex(pattern),
                Criteria.where("customer.fullName").regex(pattern),
                Criteria.where("customer.email").regex(pattern),
                Criteria.where("items.productCode").regex(pattern)));
        }

        return criteria.isEmpty() ? new Query() : new Query(new Criteria().andOperator(criteria));
    }
}