package com.oilcommerce.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResultDto {
    private boolean valid;
    private String code;
    private String description;
    private String type;
    private BigDecimal value;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
    private String message;
}
