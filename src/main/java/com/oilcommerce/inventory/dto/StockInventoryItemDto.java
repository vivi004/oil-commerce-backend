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
public class StockInventoryItemDto {
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String primaryImage;
    private String brand;
    private String category;
    private String sku;
    private String variantSize;
    private Double sellingPrice;
    private Double mrp;
    private int stockQuantity;
    private int reorderLevel;
    private String batchNumber;
    private String warehouseLocation;
    private String status; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
}
