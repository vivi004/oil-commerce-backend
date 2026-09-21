package com.oilcommerce.user.service;

import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.fileupload.service.FileUploadService;
import com.oilcommerce.user.dto.*;
import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.mapper.UserMapper;
import com.oilcommerce.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
    }

    public UserDto getProfile(UUID userId) {
        return userMapper.toDto(findById(userId));
    }

    @Transactional
    public UserDto updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = findById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }
        User user = findById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public UserDto uploadAvatar(UUID userId, MultipartFile file) {
        User user = findById(userId);
        Map<String, String> uploadResult = fileUploadService.uploadFile(file);
        user.setAvatar(uploadResult.get("url"));
        return userMapper.toDto(userRepository.save(user));
    }

    public java.util.List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public UserDto adminUpdateUser(UUID targetUserId, AdminUpdateUserRequest request) {
        User user = findById(targetUserId);

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new BusinessException("Email address is already in use by another account");
                }
                user.setEmail(newEmail);
            }
        }

        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName().trim());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
            String newPass = request.getNewPassword().trim();
            if (newPass.length() < 6) {
                throw new BusinessException("Password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(newPass));
        }

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                user.setRole(com.oilcommerce.user.entity.UserRole.valueOf(request.getRole().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid role string
            }
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto adminCreateUser(AdminCreateUserRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("A user with email " + email + " already exists.");
        }

        com.oilcommerce.user.entity.UserRole role = com.oilcommerce.user.entity.UserRole.TENANT_ADMIN;
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                role = com.oilcommerce.user.entity.UserRole.valueOf(request.getRole().trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        boolean isActive = request.getActive() == null || Boolean.TRUE.equals(request.getActive());
        User user = User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName() != null ? request.getLastName().trim() : "")
                .email(email)
                .password(passwordEncoder.encode(request.getPassword().trim()))
                .phone(request.getPhone() != null ? request.getPhone().trim() : "")
                .role(role)
                .emailVerified(true)
                .active(isActive)
                .build();

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void adminDeleteUser(UUID targetUserId) {
        User user = findById(targetUserId);
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    private User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}

