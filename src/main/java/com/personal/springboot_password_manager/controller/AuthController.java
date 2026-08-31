package com.personal.springboot_password_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.personal.springboot_password_manager.dto.request.RegisterRequestDTO;
import com.personal.springboot_password_manager.dto.response.AuthResponse;
import com.personal.springboot_password_manager.dto.response.GlobalResponse;
import com.personal.springboot_password_manager.service.AuthService;
import com.personal.springboot_password_manager.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome!";
    }

    @PostMapping("/register")
    public GlobalResponse<AuthResponse> register(@RequestBody RegisterRequestDTO request) {
        boolean userExists = userService.userExistByEmail(request.getEmail()).isPresent();
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists");
        }
        AuthResponse authRes = authService.registerUser(request);
        GlobalResponse<AuthResponse> res = new GlobalResponse<AuthResponse>(HttpStatus.OK.value(),
                "User created successfully", authRes);

        return res;
    }
}
