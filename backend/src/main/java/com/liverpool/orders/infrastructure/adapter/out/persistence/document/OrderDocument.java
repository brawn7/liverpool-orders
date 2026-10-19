package com.liverpool.orders.infrastructure.adapter.out.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
@CompoundIndex(name = "idx_orders_customer_user", def = "{'customer.userId': 1}")
public class OrderDocument {

    @Id
    private String id;
    @Indexed(unique = true, name = "uk_orders_number")
    private String orderNumber;
    private CustomerEmbeddable customer;
    private AddressEmbeddable shippingAddress;
    private List<OrderItemEmbeddable> items;
    @Indexed(name = "idx_orders_status")
    private String status;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal total;
    @Indexed(name = "idx_orders_created_at")
    private Instant createdAt;
    private Instant updatedAt;

    public OrderDocument(String id, String orderNumber, CustomerEmbeddable customer, AddressEmbeddable shippingAddress, List<OrderItemEmbeddable> items, String status, BigDecimal total, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public CustomerEmbeddable getCustomer() { return customer; }
    public AddressEmbeddable getShippingAddress() { return shippingAddress; }
    public List<OrderItemEmbeddable> getItems() { return items; }
    public String getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}