package com.personal.springboot_password_manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.personal.springboot_password_manager.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
