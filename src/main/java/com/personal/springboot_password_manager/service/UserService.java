package com.personal.springboot_password_manager.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.personal.springboot_password_manager.model.User;
import com.personal.springboot_password_manager.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> userExistByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }
}
