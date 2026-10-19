package com.liverpool.orders.infrastructure.adapter.out.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;
    private String firstName;
    private String paternalLastName;
    private String maternalLastName;
    @Indexed(unique = true, name = "uk_users_email")
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDocument(String id, String firstName, String paternalLastName, String maternalLastName, String email, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.paternalLastName = paternalLastName;
        this.maternalLastName = maternalLastName;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getPaternalLastName() { return paternalLastName; }
    public String getMaternalLastName() { return maternalLastName; }
    public String getEmail() { return email; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}