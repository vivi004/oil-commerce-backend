package com.oilcommerce.product.dto;
import com.oilcommerce.product.entity.ProductStatus;
import lombok.Builder; import lombok.Data;
import java.math.BigDecimal; import java.util.UUID;

@Data @Builder
public class ProductVariantDto {
    private UUID id;
    private String code, label, sku, barcode, imageUrl;
    private BigDecimal mrp, sellingPrice, discountPercent, gstPercent;
    private int stockQuantity;
    private boolean enabled;
    private ProductStatus status;
}
