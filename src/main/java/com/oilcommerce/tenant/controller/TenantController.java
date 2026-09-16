package com.oilcommerce.tenant.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.tenant.dto.SubscriptionPlanDto;
import com.oilcommerce.tenant.dto.TenantDto;
import com.oilcommerce.tenant.dto.TenantRequest;
import com.oilcommerce.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Tenants", description = "Multi-tenant mill management & SaaS subscription plans")
@RestController
@RequestMapping({"/tenants", "/admin/tenants"})
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "Get all tenants")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TenantDto>>> getAllTenants() {
        return ResponseEntity.ok(ApiResponse.success(tenantService.getAllTenants()));
    }

    @Operation(summary = "Get tenant by ID or slug")
    @GetMapping("/{idOrSlug}")
    public ResponseEntity<ApiResponse<TenantDto>> getTenant(@PathVariable String idOrSlug) {
        try {
            UUID id = UUID.fromString(idOrSlug);
            return ResponseEntity.ok(ApiResponse.success(tenantService.getTenantById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.success(tenantService.getTenantBySlug(idOrSlug)));
        }
    }

    @Operation(summary = "Create new tenant mill")
    @PostMapping
    public ResponseEntity<ApiResponse<TenantDto>> createTenant(@Valid @RequestBody TenantRequest request) {
        TenantDto created = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tenant created successfully", created));
    }

    @Operation(summary = "Update tenant details")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TenantDto>> updateTenant(
            @PathVariable UUID id, @Valid @RequestBody TenantRequest request) {
        TenantDto updated = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Tenant updated successfully", updated));
    }

    @Operation(summary = "Update tenant status (ACTIVE / SUSPENDED / PENDING)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TenantDto>> updateTenantStatus(
            @PathVariable UUID id, @RequestBody Map<String, String> body) {
        String status = body.getOrDefault("status", "ACTIVE");
        TenantDto updated = tenantService.updateTenantStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Tenant status updated", updated));
    }

    @Operation(summary = "Delete tenant")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.ok(ApiResponse.success("Tenant deleted successfully", null));
    }

    @Operation(summary = "Get all SaaS subscription plans")
    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(tenantService.getAllSubscriptionPlans()));
    }

    @Operation(summary = "Create subscription plan")
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> createPlan(@Valid @RequestBody SubscriptionPlanDto planDto) {
        SubscriptionPlanDto created = tenantService.createSubscriptionPlan(planDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription plan created", created));
    }

    @Operation(summary = "Update subscription plan")
    @PutMapping("/plans/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> updatePlan(
            @PathVariable UUID id, @Valid @RequestBody SubscriptionPlanDto planDto) {
        SubscriptionPlanDto updated = tenantService.updateSubscriptionPlan(id, planDto);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan updated", updated));
    }
}
