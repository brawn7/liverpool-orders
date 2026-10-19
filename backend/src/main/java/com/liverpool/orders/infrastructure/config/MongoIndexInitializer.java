package com.liverpool.orders.infrastructure.config;

import com.liverpool.orders.infrastructure.adapter.out.persistence.document.OrderDocument;
import com.liverpool.orders.infrastructure.adapter.out.persistence.document.UserDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoIndexInitializer {

    private static final Logger log = LoggerFactory.getLogger(MongoIndexInitializer.class);
    private static final List<Class<?>> DOCUMENTS = List.of(UserDocument.class, OrderDocument.class);

    private final MongoTemplate mongoTemplate;

    public MongoIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        var resolver = new MongoPersistentEntityIndexResolver(
                mongoTemplate.getConverter().getMappingContext());

        for (Class<?> type : DOCUMENTS) {
            IndexOperations ops = mongoTemplate.indexOps(type);
            resolver.resolveIndexFor(type).forEach(ops::createIndex);
            log.info("Índices verificados para {}: {}", type.getSimpleName(),
                    ops.getIndexInfo().stream().map(i -> i.getName()).toList());
        }
    }
}