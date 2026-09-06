package com.personal.springboot_password_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.personal.springboot_password_manager.dto.request.RegisterRequestDTO;
import com.personal.springboot_password_manager.dto.response.AuthResponse;
import com.personal.springboot_password_manager.model.User;
import com.personal.springboot_password_manager.model.User.UserRole;
import com.personal.springboot_password_manager.repository.UserRepository;
import com.personal.springboot_password_manager.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("registerUser::ValidRequest_CreatesUserAndReturnsAuthResponse")
    void registerUser_Success_ShouldCreateUserAndReturnAuthResponse() {
        // Arrange
        String testEmail = "test@example.com";
        String testPassword = "test_password";

        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail(testEmail);
        registerRequest.setPassword(testPassword);

        String hashedPassword = "hashed-password";
        when(passwordEncoder.encode(testPassword)).thenReturn(hashedPassword);

        String token = "jwt-token";
        when(jwtUtil.generateToken(any(String.class))).thenReturn(token);

        // Act
        AuthResponse res = authService.registerUser(registerRequest);

        // Assert
        assertNotNull(res);
        assertEquals(testEmail, res.getEmail());
        assertEquals(token, res.getToken());

        // Verify that the password was encoded before saving
        verify(passwordEncoder).encode(testPassword);
        // Verify that the user was saved
        verify(userRepository).save(any(User.class));
        // Verify that a JWT token was generated for the newly created user
        verify(jwtUtil).generateToken(any(String.class));
    }

    @Test
    @DisplayName("registerUser::ValidRequest_SavesUserWithCorrectDDetails")
    void registerUser_Success_shouldSaveUserWithCorrectDetails() {
        // Arrange
        String testEmail = "test@example.com";
        String testPassword = "test_password";

        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setEmail(testEmail);
        registerRequest.setPassword(testPassword);

        String hashedPassword = "hashed-password";
        String token = "jwt-token";

        when(passwordEncoder.encode(testPassword)).thenReturn(hashedPassword);
        when(jwtUtil.generateToken(any(String.class))).thenReturn(token);

        // Act
        authService.registerUser(registerRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(testEmail, savedUser.getEmail());
        assertEquals(hashedPassword, savedUser.getPasswordHash());
        assertEquals(Set.of(UserRole.ROLE_USER), savedUser.getRoles());
        assertNotNull(savedUser.getUserId());
        assertNotNull(savedUser.getCreatedAt());
    }
}
