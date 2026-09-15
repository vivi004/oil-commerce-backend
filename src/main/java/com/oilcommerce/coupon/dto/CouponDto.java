package com.oilcommerce.coupon.dto;
import lombok.Builder; import lombok.Data;
import java.math.BigDecimal; import java.util.UUID;
@Data @Builder
public class CouponDto {
    private UUID id; private String code; private String description;
    private String type; private BigDecimal value; private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscount; private Integer usageLimit; private int usageCount;
    private boolean isActive;
}
