package com.oilcommerce.tenant.entity;

import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "owner_name")
    private String ownerName;

    @Column
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 512)
    private String logo;

    @Column(length = 50)
    private String plan;

    @Column(length = 50)
    private String status;

    @Column(precision = 10, scale = 2)
    private BigDecimal mrr;

    @Column(name = "subscription_plan_id")
    private UUID subscriptionPlanId;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
