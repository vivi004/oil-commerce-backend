package com.oilcommerce.product.repository;

import com.oilcommerce.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Optional<ProductVariant> findBySkuAndDeletedFalse(String sku);
    List<ProductVariant> findByProductIdAndDeletedFalse(UUID productId);
    List<ProductVariant> findByDeletedFalse();
}
