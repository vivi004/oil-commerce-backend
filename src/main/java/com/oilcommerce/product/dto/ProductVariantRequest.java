package com.oilcommerce.product.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*; import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductVariantRequest {
    @NotBlank private String code;
    @NotBlank private String label;
    @NotNull @DecimalMin("0.01") private BigDecimal mrp;
    @NotNull @DecimalMin("0.01") private BigDecimal sellingPrice;
    private BigDecimal discountPercent;
    private BigDecimal gstPercent = new BigDecimal("5.00");
    @NotBlank private String sku;
    private String barcode;
    @Min(0) private int stockQuantity;
    private boolean enabled = true;
    private String imageUrl;
}
