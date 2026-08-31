package com.personal.springboot_password_manager.model;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String userId;
    private String email;
    private String passwordHash;
    private Set<UserRole> roles;
    private Long createdAt;

    public enum UserRole {
        ROLE_USER,
        ROLE_ADMIN
    }

    public User() {
    }

    public User(String userId, String email, String passwordHash, Set<UserRole> roles, Long createdAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.createdAt = createdAt;
    }
}
