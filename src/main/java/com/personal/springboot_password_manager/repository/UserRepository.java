package com.personal.springboot_password_manager.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.personal.springboot_password_manager.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
