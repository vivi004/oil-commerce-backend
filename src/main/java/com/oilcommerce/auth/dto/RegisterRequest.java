package com.oilcommerce.auth.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String confirmPassword;
    private String phone;
    @AssertTrue private boolean agreeToTerms;
}
