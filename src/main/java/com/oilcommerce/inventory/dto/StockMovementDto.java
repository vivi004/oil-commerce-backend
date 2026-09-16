package com.oilcommerce.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementDto {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String sku;
    private String variantSize;
    private String type; // STOCK_IN, STOCK_OUT, ADJUSTMENT
    private int quantity;
    private int previousStock;
    private int newStock;
    private String warehouseLocation;
    private String reason;
    private String referenceId;
    private String performedBy;
    private String createdAt;
}
