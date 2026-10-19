package com.liverpool.orders.infrastructure.adapter.out.persistence.document;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import java.math.BigDecimal;

public record OrderItemEmbeddable(
        String productCode,
        int quantity,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal price) {
}