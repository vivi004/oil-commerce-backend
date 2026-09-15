package com.oilcommerce.auth.dto;
import com.oilcommerce.user.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AuthResponse {
    private UserDto user;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
