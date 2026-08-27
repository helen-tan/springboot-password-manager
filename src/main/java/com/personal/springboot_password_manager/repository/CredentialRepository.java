package com.personal.springboot_password_manager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.personal.springboot_password_manager.model.Credential;

public interface CredentialRepository extends MongoRepository<Credential, String> {

}
