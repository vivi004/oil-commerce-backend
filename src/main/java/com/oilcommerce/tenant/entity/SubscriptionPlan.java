package com.oilcommerce.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String tier;

    @Column(name = "price_monthly", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceMonthly;

    @Column(name = "price_annual", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAnnual;

    @Column(name = "max_products", nullable = false)
    private Integer maxProducts;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(name = "max_orders_per_month", nullable = false)
    private Integer maxOrdersPerMonth;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
