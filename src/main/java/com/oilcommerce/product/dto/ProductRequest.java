package com.oilcommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oilcommerce.product.entity.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal; import java.util.List; import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRequest {
    @NotBlank private String name;
    @NotBlank private String description;
    private String shortDescription;
    @NotNull @DecimalMin("0.01") private BigDecimal price;
    private BigDecimal compareAtPrice;
    @NotBlank private String sku;
    private String barcode;
    @Min(0) private int stock;
    @Min(0) private int lowStockThreshold = 5;
    private List<String> images;
    private String thumbnail;
    @NotNull private UUID categoryId;
    private UUID brandId;
    private List<String> tags;
    private List<String> benefits;
    private String extractionMethod;
    private String smokePoint;
    private String purity;
    private String shelfLife;
    private String origin;
    private boolean featured;
    private boolean onSale;
    private boolean bestSeller;
    private ProductStatus status = ProductStatus.ACTIVE;
    private String seoTitle;
    private String seoDescription;
    private List<ProductVariantRequest> weightVariants;
}
