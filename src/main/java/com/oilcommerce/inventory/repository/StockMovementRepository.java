package com.oilcommerce.inventory.repository;

import com.oilcommerce.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByProductIdAndDeletedFalseOrderByCreatedAtDesc(UUID productId);
    List<StockMovement> findByVariantIdAndDeletedFalseOrderByCreatedAtDesc(UUID variantId);
    Page<StockMovement> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    List<StockMovement> findTop100ByDeletedFalseOrderByCreatedAtDesc();
}
