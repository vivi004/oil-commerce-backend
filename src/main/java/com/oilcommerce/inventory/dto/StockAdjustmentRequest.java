package com.oilcommerce.inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentRequest {
    @NotNull(message = "Product ID is required")
    private UUID productId;

    private UUID variantId;
    private String sku;
    private String type; // STOCK_IN, STOCK_OUT, ADJUSTMENT
    private int quantityChange;
    private String warehouseLocation;
    private String reason;
    private String referenceId;
    private String performedBy;
}
