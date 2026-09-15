package com.oilcommerce.dashboard.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.dashboard.dto.DashboardSummaryDto;
import com.oilcommerce.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name="Admin Dashboard") @RestController @RequestMapping("/admin/dashboard")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN','ORDER_MANAGER','INVENTORY_MANAGER','ACCOUNTANT')")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/summary") public ResponseEntity<ApiResponse<DashboardSummaryDto>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getSummary()));
    }
}
