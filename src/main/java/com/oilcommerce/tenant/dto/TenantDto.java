package com.oilcommerce.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDto {
    private UUID id;
    private String name;
    private String slug;
    private String businessName;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String logo;
    private String status;
    private UUID subscriptionPlanId;
    private String subscriptionPlanName;
    private String planTier;
    private Double mrr;
    private int totalOrders;
    private int productCount;
    private boolean active;
    private String createdAt;
}
