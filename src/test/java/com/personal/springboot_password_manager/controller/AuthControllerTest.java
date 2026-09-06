package com.personal.springboot_password_manager.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.personal.springboot_password_manager.dto.request.RegisterRequestDTO;
import com.personal.springboot_password_manager.dto.response.AuthResponse;
import com.personal.springboot_password_manager.model.User;
import com.personal.springboot_password_manager.security.JwtUtil;
import com.personal.springboot_password_manager.service.AuthService;
import com.personal.springboot_password_manager.service.UserService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("register::ValidEmail_CreatesUser")
    void register_Success_shouldRegisterUserSuccessfully() throws Exception {
        // Arrange
        String testEmail = "test@example.com";
        String testPassword = "test_password";

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail(testEmail);
        req.setPassword(testPassword);

        // Email does not exist
        when(userService.userExistByEmail(testEmail)).thenReturn(Optional.empty());

        AuthResponse authRes = new AuthResponse("jwt-token", "testUserId", testEmail);

        // Registration succeeds
        when(authService.registerUser(any(RegisterRequestDTO.class))).thenReturn(authRes);

        // Act
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.userId").value("testUserId"))
                .andExpect(jsonPath("$.data.email").value(testEmail));

        // Verify
        verify(userService).userExistByEmail(testEmail);
        verify(authService).registerUser(any(RegisterRequestDTO.class));
    }

    @Test
    @DisplayName("register::EmailExists_Throw409Conflict")
    void register_Fail_shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Arrange
        String testEmail = "alreadyExist@example.com";
        String testPassword = "test_password";

        RegisterRequestDTO req = new RegisterRequestDTO();
        req.setEmail(testEmail);
        req.setPassword(testPassword);

        User existingUser = new User();
        existingUser.setEmail(testEmail);

        // Email already exists
        when(userService.userExistByEmail(testEmail)).thenReturn(Optional.of(existingUser));

        // Act
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.statusCode").value(409))
                .andExpect(jsonPath("$.message").value("A user with this email already exists"));

        // Assert
        // Verify that userExisByEmail is called - controller checked if email exists
        verify(userService).userExistByEmail(testEmail);

        // Verify that registerUser() was not called - that registration was not
        // attempted
        verify(authService, never()).registerUser(any(RegisterRequestDTO.class));
    }
}
