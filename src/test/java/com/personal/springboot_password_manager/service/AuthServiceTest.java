package com.personal.springboot_password_manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.personal.springboot_password_manager.dto.request.LoginRequestDTO;
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

    @Test
    @DisplayName("login::ValidCredentials_ReturnsAuthResponse")
    void login_shouldReturnAuthResponseForValidCredentials() {
        // Arrange
        String testEmail = "test@example.com";
        String testPassword = "test_password";

        User user = new User();
        user.setUserId("user1");
        user.setEmail(testEmail);
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(testPassword, "hashed-password")).thenReturn(true);

        when(jwtUtil.generateToken("user1")).thenReturn("jwt-token");

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("user1", response.getUserId());
        assertEquals(testEmail, response.getEmail());

        // Verify that the user was looked up
        verify(userRepository).findByEmail(testEmail);

        // Verify that the supplied password was checked against the stored hash
        verify(passwordEncoder).matches(testPassword, "hashed-password");

        // Verify that a JWT was generated after successful authentication
        verify(jwtUtil).generateToken("user1");
    }

    @Test
    @DisplayName("login::EmailDoesNotExist_ThrowsBadRequest")
    void login_Fail_shouldThrowBadRequestWhenEmailDoesNotExist() {
        // Arrange
        String doesNotExist = "doesNotExist@example.com";
        String testPassword = "test_password";
        when(userRepository.findByEmail(doesNotExist))
                .thenReturn(Optional.empty());

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(doesNotExist);
        loginRequest.setPassword(testPassword);

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());

        // Verify that password checking was never reached
        verify(passwordEncoder, never()).matches(any(String.class), any(String.class));
        // Verify that no JWT was generated
        verify(jwtUtil, never()).generateToken(any(String.class));
    }

    @Test
    @DisplayName("login::InvalidPassword_ThrowsBadRequest")
    void login_Fail_shouldThrowBadRequestWhenPasswordIsInvalid() {
        // Arrange
        String testEmail = "test@example.com";
        String wrongPassword = "wrong_password";

        User user = new User();
        user.setUserId("user1");
        user.setEmail(testEmail);
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail(testEmail))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(wrongPassword, "hashed-password")).thenReturn(false);

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(wrongPassword);

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest));

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());

        // Verify that password validation was done
        verify(passwordEncoder).matches(wrongPassword, "hashed-password");
        // Verify that no JWT was generated for an invalid password
        verify(jwtUtil, never()).generateToken(any(String.class));
    }

}
