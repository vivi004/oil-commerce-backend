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

    @Column(nullable = false) private String code; // 100ml, 500ml, 1L, etc.
    @Column(nullable = false) private String label;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal mrp;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal sellingPrice;
    @Column(precision = 5, scale = 2) private BigDecimal discountPercent;
    @Column(precision = 5, scale = 2) @Builder.Default private BigDecimal gstPercent = new BigDecimal("5.00");
    @Column(nullable = false, unique = true) private String sku;
    @Column private String barcode;
    @Column(nullable = false) @Builder.Default private int stockQuantity = 0;
    @Column @Builder.Default private boolean enabled = true;
    @Column private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
}
