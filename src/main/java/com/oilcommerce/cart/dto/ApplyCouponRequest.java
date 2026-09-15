package com.oilcommerce.cart.dto;
import jakarta.validation.constraints.NotBlank; import lombok.Data;
@Data public class ApplyCouponRequest { @NotBlank private String couponCode; }
