package com.oilcommerce.auth.service;

import com.oilcommerce.auth.dto.*;
import com.oilcommerce.user.dto.UserDto;
import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.mapper.UserMapper;
import com.oilcommerce.user.repository.UserRepository;
import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final com.oilcommerce.common.email.EmailService emailService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cleanEmail, request.getPassword()));

        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", cleanEmail));

        user.setLastLoginAt(Instant.now());
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .user(userMapper.toDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900000L)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new BusinessException("Email already registered", HttpStatus.CONFLICT);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(cleanEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .emailVerificationToken(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        log.info("New user registered: {}", user.getEmail());

        return AuthResponse.builder()
                .user(userMapper.toDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900000L)
                .build();
    }

    @Transactional
    public void logout(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
        });
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BusinessException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("Refresh token not found", HttpStatus.UNAUTHORIZED));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .user(userMapper.toDto(user))
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(900000L)
                .build();
    }

    public record ForgotPasswordResult(
            boolean userFound,
            boolean emailSent,
            String token,
            String message,
            int statusCode
    ) {}

    @Transactional
    public ForgotPasswordResult forgotPassword(ForgotPasswordRequest request) {
        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        log.info("--------------------------------------------------------------------------------");
        log.info("[Forgot Password Flow] Step 1/4: Looking up user account for email: '{}'", cleanEmail);

        if (cleanEmail.isBlank()) {
            log.error("[Forgot Password Flow] Error: Email provided in request is blank or null!");
            return new ForgotPasswordResult(false, false, null, "Email is required", 400);
        }

        java.util.Optional<User> userOpt = userRepository.findByEmailIgnoreCase(cleanEmail);
        if (userOpt.isEmpty()) {
            log.warn("[Forgot Password Flow] User lookup failed: Email '{}' does not exist in database.", cleanEmail);
            log.info("--------------------------------------------------------------------------------");
            return new ForgotPasswordResult(false, false, null, "If this email exists, a reset link has been sent", 200);
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(3600);
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(expiry);
        userRepository.save(user);

        log.info("[Forgot Password Flow] Step 2/4: Generated reset token for user ID '{}' ({})", user.getId(), user.getEmail());
        log.info("[Forgot Password Flow] Token: '{}' (expires in 1 hour at {})", token, expiry);
        log.info("[Forgot Password Flow] Step 3/4: Successfully persisted reset token to database.");
        log.info("[Forgot Password Flow] Step 4/4: Executing synchronous email dispatch for: '{}'", user.getEmail());

        // Synchronous dispatch with strict timeout to give deterministic result back to controller
        com.oilcommerce.common.email.EmailService.EmailSendResult emailResult =
                emailService.sendPasswordResetEmail(user.getEmail(), token);

        if (emailResult.success()) {
            log.info("✅ [Forgot Password Flow] Email successfully accepted by provider for: {}", user.getEmail());
            log.info("--------------------------------------------------------------------------------");
            return new ForgotPasswordResult(true, true, token, "Password reset email sent successfully", 200);
        } else {
            log.error("❌ [Forgot Password Flow] Email dispatch failed for {}: {}", user.getEmail(), emailResult.message());
            log.info("--------------------------------------------------------------------------------");
            return new ForgotPasswordResult(true, false, token, emailResult.message(), emailResult.statusCode());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or expired reset token"));
        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
            throw new BusinessException("Reset token has expired");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BusinessException("Invalid verification token"));
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);
    }

    public UserDto getMe(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return userMapper.toDto(user);
    }
}
