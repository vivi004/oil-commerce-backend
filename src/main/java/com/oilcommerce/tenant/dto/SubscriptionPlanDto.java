package com.oilcommerce.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanDto {
    private UUID id;
    private String name;
    private String tier;
    private Double priceMonthly;
    private Double priceAnnual;
    private Integer maxProducts;
    private Integer maxUsers;
    private Integer maxOrdersPerMonth;
    private List<String> features;
    private boolean isActive;
}
