package com.oilcommerce.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportSummaryDto {
    private double totalLiquidVolumeLiters;
    private double totalSolidAgroKg;
    private double averageCartValue;
    private double totalRevenue;
    private int totalOrders;
    private List<SkuSalesPerformanceDto> topSellingSkus;
    private List<CategoryVolumeReportDto> categoryVolumes;
}
