package com.oilcommerce.coupon.entity;

import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal; import java.time.Instant;

@Entity @Table(name = "coupons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coupon extends BaseEntity {
    @Column(nullable = false, unique = true) private String code;
    @Column private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private CouponType type;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal value;
    @Column(precision = 10, scale = 2) private BigDecimal minimumOrderAmount;
    @Column(precision = 10, scale = 2) private BigDecimal maximumDiscount;
    @Column private Integer usageLimit;
    @Column @Builder.Default private int usageCount = 0;
    @Column private Integer perUserLimit;
    @Column @Builder.Default private boolean active = true;
    @Column private Instant validFrom;
    @Column private Instant validUntil;
}
