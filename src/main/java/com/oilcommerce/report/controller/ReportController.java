package com.oilcommerce.report.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.report.dto.CategoryVolumeReportDto;
import com.oilcommerce.report.dto.SalesReportSummaryDto;
import com.oilcommerce.report.dto.SkuSalesPerformanceDto;
import com.oilcommerce.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Reports", description = "Oil extraction volume, SKU sales performance, and category analytics")
@RestController
@RequestMapping({"/reports", "/admin/reports"})
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get overall sales & extraction summary report")
    @GetMapping("/sales-summary")
    public ResponseEntity<ApiResponse<SalesReportSummaryDto>> getSalesSummary() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSalesReportSummary()));
    }

    @Operation(summary = "Get top selling product SKUs and margins")
    @GetMapping("/top-skus")
    public ResponseEntity<ApiResponse<List<SkuSalesPerformanceDto>>> getTopSkus() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSalesReportSummary().getTopSellingSkus()));
    }

    @Operation(summary = "Get category revenue and extraction volumes")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryVolumeReportDto>>> getCategoryVolumes() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getSalesReportSummary().getCategoryVolumes()));
    }
}
