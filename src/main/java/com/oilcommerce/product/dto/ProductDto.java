package com.oilcommerce.product.dto;

import com.oilcommerce.product.entity.ProductStatus;
import lombok.Builder; import lombok.Data;
import java.math.BigDecimal; import java.time.Instant; import java.util.List; import java.util.UUID;

@Data @Builder
public class ProductDto {
    private UUID id;
    private String name, slug, description, shortDescription, sku, barcode, thumbnail;
    private BigDecimal price, compareAtPrice;
    private Integer discount;
    private int stock, lowStockThreshold, reviewCount;
    private BigDecimal rating;
    private List<String> images, tags, benefits;
    private String extractionMethod, smokePoint, purity, shelfLife, origin;
    private String categoryId, categoryName, categorySlug;
    private String brandId, brand;
    private boolean isFeatured, isOnSale, isBestSeller;
    private ProductStatus status;
    private String seoTitle, seoDescription;
    private List<ProductVariantDto> weightVariants;
    private Instant createdAt, updatedAt;
}
