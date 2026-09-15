package com.oilcommerce.product.entity;

import com.oilcommerce.brand.entity.Brand;
import com.oilcommerce.category.entity.Category;
import com.oilcommerce.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product extends BaseEntity {

    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String slug;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(columnDefinition = "TEXT") private String shortDescription;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal price;
    @Column(precision = 10, scale = 2) private BigDecimal compareAtPrice;
    @Column(nullable = false, unique = true) private String sku;
    @Column private String barcode;
    @Column(nullable = false) @Builder.Default private int stock = 0;
    @Column @Builder.Default private int lowStockThreshold = 5;

    @Column @JdbcTypeCode(SqlTypes.JSON)
    private List<String> images;

    @Column private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column @JdbcTypeCode(SqlTypes.JSON) private List<String> tags;
    @Column @JdbcTypeCode(SqlTypes.JSON) private List<String> benefits;

    @Column private String extractionMethod;
    @Column private String smokePoint;
    @Column private String purity;
    @Column private String shelfLife;
    @Column private String origin;

    @Column(nullable = false, precision = 3, scale = 2) @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false) @Builder.Default private int reviewCount = 0;
    @Column(nullable = false) @Builder.Default private boolean featured = false;
    @Column(nullable = false) @Builder.Default private boolean onSale = false;
    @Column(nullable = false) @Builder.Default private boolean bestSeller = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column private String seoTitle;
    @Column(columnDefinition = "TEXT") private String seoDescription;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> variants;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }
}
