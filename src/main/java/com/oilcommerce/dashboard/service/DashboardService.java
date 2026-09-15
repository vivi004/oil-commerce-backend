package com.oilcommerce.dashboard.service;

import com.oilcommerce.dashboard.dto.DashboardSummaryDto;
import com.oilcommerce.order.entity.OrderStatus;
import com.oilcommerce.order.repository.OrderRepository;
import com.oilcommerce.product.entity.ProductStatus;
import com.oilcommerce.product.repository.ProductRepository;
import com.oilcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DashboardSummaryDto getSummary() {
        long totalOrders = orderRepository.countByDeletedFalse();
        long totalProducts = productRepository.count();
        long totalCustomers = userRepository.count();
        long lowStock = productRepository.findByStatusAndDeletedFalseAndStockLessThanEqual(ProductStatus.ACTIVE, 5).size();
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        long pendingOrders = orderRepository.countByStatusAndDeletedFalse(OrderStatus.PENDING);

        return DashboardSummaryDto.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .pendingOrders(pendingOrders)
                .lowStockProducts(lowStock)
                .recentOrders(List.of())
                .topProducts(List.of())
                .monthlySales(Map.of())
                .build();
    }
}
