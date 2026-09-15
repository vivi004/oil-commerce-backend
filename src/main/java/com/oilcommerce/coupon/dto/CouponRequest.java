package com.oilcommerce.coupon.dto;
import jakarta.validation.constraints.NotBlank; import lombok.Data;
import java.math.BigDecimal;
@Data
public class CouponRequest {
    @NotBlank private String code; private String description;
    private String type = "PERCENT"; private BigDecimal value;
    private BigDecimal minimumOrderAmount; private BigDecimal maximumDiscount;
    private Integer usageLimit; private boolean active = true;
    private String validFrom; private String validUntil;
}
