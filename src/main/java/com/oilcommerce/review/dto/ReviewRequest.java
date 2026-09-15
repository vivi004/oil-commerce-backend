package com.oilcommerce.review.dto;
import jakarta.validation.constraints.*; import lombok.Data; import java.math.BigDecimal;
@Data
public class ReviewRequest {
    @NotNull @DecimalMin("1") @DecimalMax("5") private BigDecimal rating;
    private String title; @NotBlank private String body;
}
