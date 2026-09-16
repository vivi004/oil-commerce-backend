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
public class CategoryVolumeReportDto {
    private UUID categoryId;
    private String name;
    private double volumeLiters;
    private double revenue;
    private int orderCount;
    private int sharePercent;
}
