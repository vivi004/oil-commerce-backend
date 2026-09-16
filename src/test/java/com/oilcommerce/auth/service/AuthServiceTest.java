package com.oilcommerce.auth.service;

import com.oilcommerce.auth.dto.ForgotPasswordRequest;
import com.oilcommerce.common.email.EmailService;
import com.oilcommerce.security.JwtTokenProvider;
import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.mapper.UserMapper;
import com.oilcommerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;

    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(
                userRepository,
                userMapper,
                passwordEncoder,
                jwtTokenProvider,
                authenticationManager,
                emailService
        );
        sampleUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("encoded_pass")
                .build();
        sampleUser.setId(UUID.randomUUID());
    }

    @Test
    void testForgotPassword_UserExists_EmailSucceeds() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(emailService.sendPasswordResetEmail(eq("test@example.com"), anyString()))
                .thenReturn(new EmailService.EmailSendResult(true, 200, "{\"id\":\"msg_1\"}", "Accepted", 100));

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        AuthService.ForgotPasswordResult result = authService.forgotPassword(request);

        assertTrue(result.userFound());
        assertTrue(result.emailSent());
        assertNotNull(result.token());
        assertEquals(200, result.statusCode());
        verify(userRepository).save(any(User.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), anyString());
    }

    @Test
    void testForgotPassword_UserExists_EmailFailsWith403() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(emailService.sendPasswordResetEmail(eq("test@example.com"), anyString()))
                .thenReturn(new EmailService.EmailSendResult(false, 403, "{\"statusCode\":403}", "Testing restriction", 110));

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@example.com");

        AuthService.ForgotPasswordResult result = authService.forgotPassword(request);

        assertTrue(result.userFound());
        assertFalse(result.emailSent());
        assertNotNull(result.token());
        assertEquals(403, result.statusCode());
        assertTrue(result.message().contains("Testing restriction"));
    }

    @Test
    void testForgotPassword_UserNotFound() {
        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("unknown@example.com");

        AuthService.ForgotPasswordResult result = authService.forgotPassword(request);

        assertFalse(result.userFound());
        assertFalse(result.emailSent());
        assertNull(result.token());
        assertEquals(200, result.statusCode());
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }
}
