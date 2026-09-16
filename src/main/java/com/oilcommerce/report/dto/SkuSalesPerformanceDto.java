package com.oilcommerce.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuSalesPerformanceDto {
    private UUID productId;
    private String name;
    private String sku;
    private String size;
    private String category;
    private int unitsSold;
    private double unitPrice;
    private double grossRevenue;
    private int marginPercent;
}
