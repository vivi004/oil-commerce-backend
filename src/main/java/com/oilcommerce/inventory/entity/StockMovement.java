package com.oilcommerce.inventory.entity;

import com.oilcommerce.common.BaseEntity;
import com.oilcommerce.product.entity.Product;
import com.oilcommerce.product.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", insertable = false, updatable = false)
    private ProductVariant variant;

    @Column(name = "movement_type", nullable = false, length = 50)
    private String movementType; // STOCK_IN, STOCK_OUT, ADJUSTMENT, RETURN

    @Column(nullable = false)
    private int quantity;

    @Column(name = "previous_quantity", nullable = false)
    private int previousQuantity;

    @Column(name = "new_quantity", nullable = false)
    private int newQuantity;

    @Column(length = 512)
    private String reason;

    @Column(name = "reference_id")
    private String referenceId;
}
