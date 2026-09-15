package com.oilcommerce.user.dto;
import com.oilcommerce.user.entity.UserRole;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data @Builder
public class UserDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String avatar;
    private UserRole role;
    private boolean isEmailVerified;
    private boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
