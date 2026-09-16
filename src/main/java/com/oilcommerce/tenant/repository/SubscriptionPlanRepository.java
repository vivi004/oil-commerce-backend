package com.oilcommerce.tenant.repository;

import com.oilcommerce.tenant.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    List<SubscriptionPlan> findByActiveTrue();
    Optional<SubscriptionPlan> findByTierIgnoreCase(String tier);
}
