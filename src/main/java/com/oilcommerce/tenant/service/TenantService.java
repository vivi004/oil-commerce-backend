package com.oilcommerce.tenant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.order.repository.OrderRepository;
import com.oilcommerce.product.repository.ProductRepository;
import com.oilcommerce.tenant.dto.SubscriptionPlanDto;
import com.oilcommerce.tenant.dto.TenantDto;
import com.oilcommerce.tenant.dto.TenantRequest;
import com.oilcommerce.tenant.entity.SubscriptionPlan;
import com.oilcommerce.tenant.entity.Tenant;
import com.oilcommerce.tenant.repository.SubscriptionPlanRepository;
import com.oilcommerce.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<TenantDto> getAllTenants() {
        Map<UUID, SubscriptionPlan> planMap = subscriptionPlanRepository.findAll().stream()
                .collect(Collectors.toMap(SubscriptionPlan::getId, p -> p, (a, b) -> a));

        int totalProducts = (int) productRepository.count();
        int totalOrders = (int) orderRepository.count();

        return tenantRepository.findByDeletedFalseOrderByNameAsc().stream()
                .map(t -> mapToTenantDto(t, planMap.get(t.getSubscriptionPlanId()), totalProducts, totalOrders))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TenantDto getTenantById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
        SubscriptionPlan plan = tenant.getSubscriptionPlanId() != null
                ? subscriptionPlanRepository.findById(tenant.getSubscriptionPlanId()).orElse(null)
                : null;
        return mapToTenantDto(tenant, plan, (int) productRepository.count(), (int) orderRepository.count());
    }

    @Transactional(readOnly = true)
    public TenantDto getTenantBySlug(String slug) {
        Tenant tenant = tenantRepository.findBySlugAndDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "slug", slug));
        SubscriptionPlan plan = tenant.getSubscriptionPlanId() != null
                ? subscriptionPlanRepository.findById(tenant.getSubscriptionPlanId()).orElse(null)
                : null;
        return mapToTenantDto(tenant, plan, (int) productRepository.count(), (int) orderRepository.count());
    }

    @Transactional
    public TenantDto createTenant(TenantRequest request) {
        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = request.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        }

        if (tenantRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 1000;
        }

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .slug(slug)
                .businessName(request.getBusinessName() != null ? request.getBusinessName() : request.getName())
                .ownerName(request.getOwnerName() != null ? request.getOwnerName() : "Administrator")
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .logo(request.getLogo())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .mrr(request.getMrr() != null ? BigDecimal.valueOf(request.getMrr()) : BigDecimal.ZERO)
                .subscriptionPlanId(request.getSubscriptionPlanId())
                .active(true)
                .build();

        tenant = tenantRepository.save(tenant);

        SubscriptionPlan plan = tenant.getSubscriptionPlanId() != null
                ? subscriptionPlanRepository.findById(tenant.getSubscriptionPlanId()).orElse(null)
                : null;

        return mapToTenantDto(tenant, plan, 0, 0);
    }

    @Transactional
    public TenantDto updateTenant(UUID id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));

        if (request.getName() != null) tenant.setName(request.getName());
        if (request.getBusinessName() != null) tenant.setBusinessName(request.getBusinessName());
        if (request.getOwnerName() != null) tenant.setOwnerName(request.getOwnerName());
        if (request.getEmail() != null) tenant.setEmail(request.getEmail());
        if (request.getPhone() != null) tenant.setPhone(request.getPhone());
        if (request.getAddress() != null) tenant.setAddress(request.getAddress());
        if (request.getLogo() != null) tenant.setLogo(request.getLogo());
        if (request.getStatus() != null) tenant.setStatus(request.getStatus());
        if (request.getMrr() != null) tenant.setMrr(BigDecimal.valueOf(request.getMrr()));
        if (request.getSubscriptionPlanId() != null) tenant.setSubscriptionPlanId(request.getSubscriptionPlanId());

        tenant = tenantRepository.save(tenant);

        SubscriptionPlan plan = tenant.getSubscriptionPlanId() != null
                ? subscriptionPlanRepository.findById(tenant.getSubscriptionPlanId()).orElse(null)
                : null;

        return mapToTenantDto(tenant, plan, (int) productRepository.count(), (int) orderRepository.count());
    }

    @Transactional
    public TenantDto updateTenantStatus(UUID id, String status) {
        Tenant tenant = tenantRepository.findById(id)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));

        tenant.setStatus(status);
        tenant = tenantRepository.save(tenant);

        SubscriptionPlan plan = tenant.getSubscriptionPlanId() != null
                ? subscriptionPlanRepository.findById(tenant.getSubscriptionPlanId()).orElse(null)
                : null;

        return mapToTenantDto(tenant, plan, (int) productRepository.count(), (int) orderRepository.count());
    }

    @Transactional
    public void deleteTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
        tenant.setDeleted(true);
        tenant.setDeletedAt(Instant.now());
        tenantRepository.save(tenant);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanDto> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll().stream()
                .map(this::mapToPlanDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionPlanDto getSubscriptionPlanById(UUID id) {
        return subscriptionPlanRepository.findById(id)
                .map(this::mapToPlanDto)
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlan", "id", id));
    }

    @Transactional
    public SubscriptionPlanDto createSubscriptionPlan(SubscriptionPlanDto dto) {
        String featuresJson = "[]";
        try {
            if (dto.getFeatures() != null) {
                featuresJson = objectMapper.writeValueAsString(dto.getFeatures());
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize plan features", e);
        }

        Integer maxProducts = dto.getMaxProducts();
        if (maxProducts == null) {
            maxProducts = 50;
        }

        Integer maxUsers = dto.getMaxUsers();
        if (maxUsers == null) {
            maxUsers = 3;
        }

        Integer maxOrdersPerMonth = dto.getMaxOrdersPerMonth();
        if (maxOrdersPerMonth == null) {
            maxOrdersPerMonth = 500;
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(dto.getName())
                .tier(dto.getTier() != null ? dto.getTier() : "STARTER")
                .priceMonthly(dto.getPriceMonthly() != null ? BigDecimal.valueOf(dto.getPriceMonthly()) : BigDecimal.ZERO)
                .priceAnnual(dto.getPriceAnnual() != null ? BigDecimal.valueOf(dto.getPriceAnnual()) : BigDecimal.ZERO)
                .maxProducts(maxProducts)
                .maxUsers(maxUsers)
                .maxOrdersPerMonth(maxOrdersPerMonth)
                .features(featuresJson)
                .active(dto.isActive())
                .build();

        plan = subscriptionPlanRepository.save(plan);
        return mapToPlanDto(plan);
    }

    @Transactional
    public SubscriptionPlanDto updateSubscriptionPlan(UUID id, SubscriptionPlanDto dto) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SubscriptionPlan", "id", id));

        if (dto.getName() != null) plan.setName(dto.getName());
        if (dto.getTier() != null) plan.setTier(dto.getTier());
        if (dto.getPriceMonthly() != null) plan.setPriceMonthly(BigDecimal.valueOf(dto.getPriceMonthly()));
        if (dto.getPriceAnnual() != null) plan.setPriceAnnual(BigDecimal.valueOf(dto.getPriceAnnual()));
        if (dto.getMaxProducts() != null) plan.setMaxProducts(dto.getMaxProducts());
        if (dto.getMaxUsers() != null) plan.setMaxUsers(dto.getMaxUsers());
        if (dto.getMaxOrdersPerMonth() != null) plan.setMaxOrdersPerMonth(dto.getMaxOrdersPerMonth());
        if (dto.getFeatures() != null) {
            try {
                plan.setFeatures(objectMapper.writeValueAsString(dto.getFeatures()));
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize plan features", e);
            }
        }
        plan.setActive(dto.isActive());

        plan = subscriptionPlanRepository.save(plan);
        return mapToPlanDto(plan);
    }

    private TenantDto mapToTenantDto(Tenant t, SubscriptionPlan plan, int totalProducts, int totalOrders) {
        String planName = plan != null ? plan.getName() : "Mara Chekku Starter";
        String tier = plan != null ? plan.getTier() : (t.getPlan() != null ? t.getPlan() : "STARTER");
        Double mrrVal = t.getMrr() != null ? t.getMrr().doubleValue() : (plan != null && plan.getPriceMonthly() != null ? plan.getPriceMonthly().doubleValue() : 1499.0);

        return TenantDto.builder()
                .id(t.getId())
                .name(t.getName())
                .slug(t.getSlug())
                .businessName(t.getBusinessName() != null ? t.getBusinessName() : t.getName())
                .ownerName(t.getOwnerName() != null ? t.getOwnerName() : "Owner")
                .email(t.getEmail())
                .phone(t.getPhone())
                .address(t.getAddress())
                .logo(t.getLogo())
                .status(t.getStatus() != null ? t.getStatus() : "ACTIVE")
                .subscriptionPlanId(t.getSubscriptionPlanId())
                .subscriptionPlanName(planName)
                .planTier(tier)
                .mrr(mrrVal)
                .totalOrders(totalOrders)
                .productCount(totalProducts)
                .active(t.isActive())
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : Instant.now().toString())
                .build();
    }

    private SubscriptionPlanDto mapToPlanDto(SubscriptionPlan plan) {
        List<String> features = Collections.emptyList();
        if (plan.getFeatures() != null && !plan.getFeatures().isBlank()) {
            try {
                features = objectMapper.readValue(plan.getFeatures(), new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                features = List.of(plan.getFeatures().split(","));
            }
        }

        return SubscriptionPlanDto.builder()
                .id(plan.getId())
                .name(plan.getName())
                .tier(plan.getTier())
                .priceMonthly(plan.getPriceMonthly() != null ? plan.getPriceMonthly().doubleValue() : 0.0)
                .priceAnnual(plan.getPriceAnnual() != null ? plan.getPriceAnnual().doubleValue() : 0.0)
                .maxProducts(plan.getMaxProducts())
                .maxUsers(plan.getMaxUsers())
                .maxOrdersPerMonth(plan.getMaxOrdersPerMonth())
                .features(features)
                .isActive(plan.isActive())
                .build();
    }
}
