package com.oilcommerce.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRequest {
    @NotBlank(message = "Name is required")
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
    private Double mrr;
}
