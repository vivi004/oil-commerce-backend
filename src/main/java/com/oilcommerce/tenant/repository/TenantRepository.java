package com.oilcommerce.tenant.repository;

import com.oilcommerce.tenant.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findBySlugAndDeletedFalse(String slug);
    List<Tenant> findByDeletedFalseOrderByNameAsc();
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}
