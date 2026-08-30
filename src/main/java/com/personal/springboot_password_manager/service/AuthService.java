package com.personal.springboot_password_manager.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.personal.springboot_password_manager.dto.request.LoginRequestDTO;
import com.personal.springboot_password_manager.dto.request.RegisterRequestDTO;
import com.personal.springboot_password_manager.dto.response.AuthResponse;
import com.personal.springboot_password_manager.model.User;
import com.personal.springboot_password_manager.model.enums.UserRole;
import com.personal.springboot_password_manager.repository.UserRepository;
import com.personal.springboot_password_manager.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse registerUser(RegisterRequestDTO request) {
        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRoles(Set.of(UserRole.ROLE_USER));
        newUser.setCreatedAt(System.currentTimeMillis());

        userRepository.save(newUser);

        String token = jwtUtil.generateToken(newUser.getUserId());

        AuthResponse authRes = new AuthResponse(token, newUser.getUserId(), newUser.getEmail());

        return authRes;
    }

    public AuthResponse login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password"));

        String inputPassword = request.getPassword();
        String passwordHashInDB = user.getPasswordHash();

        boolean passwordIsValid = passwordEncoder.matches(inputPassword, passwordHashInDB);

        if (!passwordIsValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getUserId());

        AuthResponse authRes = new AuthResponse(token, user.getUserId(), user.getEmail());

        return authRes;
    }

}
