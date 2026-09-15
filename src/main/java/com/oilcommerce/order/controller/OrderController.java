package com.oilcommerce.order.controller;

import com.oilcommerce.common.ApiResponse; import com.oilcommerce.common.PaginatedResponse;
import com.oilcommerce.order.dto.*; import com.oilcommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@Tag(name="Orders") @RestController @RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;
    private UUID uid(UserDetails ud) { return UUID.fromString(ud.getUsername()); }

    @GetMapping("/orders") public ResponseEntity<ApiResponse<PaginatedResponse<OrderDto>>> getOrders(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="10") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(uid(ud),page,pageSize)));
    }

    @PostMapping("/orders") public ResponseEntity<ApiResponse<OrderDto>> create(
            @AuthenticationPrincipal UserDetails ud, @Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order placed",orderService.createOrder(uid(ud),req)));
    }

    @GetMapping("/orders/{id}") public ResponseEntity<ApiResponse<OrderDto>> getById(
            @AuthenticationPrincipal UserDetails ud, @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(uid(ud),id)));
    }

    @PostMapping("/orders/{id}/cancel") public ResponseEntity<ApiResponse<OrderDto>> cancel(
            @AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled",orderService.cancelOrder(uid(ud),id)));
    }

    @PostMapping("/orders/{id}/return") public ResponseEntity<ApiResponse<OrderDto>> returnOrder(
            @AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Return requested",orderService.returnOrder(uid(ud),id)));
    }

    @GetMapping("/orders/{id}/tracking") public ResponseEntity<ApiResponse<List<StatusHistoryDto>>> tracking(
            @AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getTracking(uid(ud),id)));
    }

    // Admin endpoints
    @GetMapping("/admin/orders")
    @PreAuthorize("hasAnyRole('ORDER_MANAGER','TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<OrderDto>>> getAllOrders(
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="20") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAllOrders(page,pageSize)));
    }

    @PutMapping("/admin/orders/{id}/status")
    @PreAuthorize("hasAnyRole('ORDER_MANAGER','TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> updateStatus(
            @PathVariable UUID id, @RequestBody UpdateOrderStatusRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated",orderService.updateOrderStatus(id,req)));
    }
}
