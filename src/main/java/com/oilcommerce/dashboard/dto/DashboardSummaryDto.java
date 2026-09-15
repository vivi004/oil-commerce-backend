package com.oilcommerce.dashboard.dto;
import lombok.Builder; import lombok.Data;
import java.math.BigDecimal; import java.util.List; import java.util.Map;

@Data @Builder
public class DashboardSummaryDto {
    private BigDecimal totalRevenue; private long totalOrders;
    private long totalCustomers; private long totalProducts;
    private long pendingOrders; private long lowStockProducts;
    private List<Map<String,Object>> recentOrders;
    private List<Map<String,Object>> topProducts;
    private Map<String,BigDecimal> monthlySales;
}
