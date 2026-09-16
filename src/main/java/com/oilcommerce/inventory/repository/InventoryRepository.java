package com.oilcommerce.inventory.repository;

import com.oilcommerce.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findByVariantIdAndDeletedFalse(UUID variantId);
    List<Inventory> findByProductIdAndDeletedFalse(UUID productId);
    List<Inventory> findByDeletedFalse();
}
