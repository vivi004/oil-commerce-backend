package com.oilcommerce.product.entity;

import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false) private String code;
    @Column(nullable = false) private String label;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal mrp;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal sellingPrice;
    @Column(precision = 5, scale = 2) private BigDecimal discountPercent;
    @Column(nullable = false, unique = true) private String sku;
    @Column private String barcode;
    @Builder.Default
    @Column(nullable = false) private int stockQuantity = 0;
    @Builder.Default
    @Column private boolean enabled = true;
    @Column private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;
}
