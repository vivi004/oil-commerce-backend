package com.oilcommerce.coupon.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.coupon.dto.*;
import com.oilcommerce.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Coupons", description = "Promotional coupons & checkout discount validation")
@RestController
@RequestMapping({"/coupons", "/admin/coupons"})
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "Get all active promotional coupons")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponDto>>> getActiveCoupons() {
        return ResponseEntity.ok(ApiResponse.success(couponService.getActiveCoupons()));
    }

    @Operation(summary = "Get coupon details by code")
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponDto>> getCouponByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(couponService.getCouponByCode(code)));
    }

    @Operation(summary = "Validate coupon and calculate instant checkout discount")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<CouponValidationResultDto>> validateCoupon(
            @RequestBody ValidateCouponRequest req) {
        try {
            BigDecimal subtotal = req.getSubtotal() != null ? req.getSubtotal() : BigDecimal.ZERO;
            CouponDto coupon = couponService.getCouponByCode(req.getCode());
            BigDecimal discount = couponService.calculateDiscountForAmount(req.getCode(), subtotal);
            BigDecimal finalTotal = subtotal.subtract(discount).max(BigDecimal.ZERO);

            CouponValidationResultDto result = CouponValidationResultDto.builder()
                    .valid(true)
                    .code(coupon.getCode())
                    .description(coupon.getDescription())
                    .type(coupon.getType())
                    .value(coupon.getValue())
                    .discountAmount(discount)
                    .finalTotal(finalTotal)
                    .message("Promo code " + coupon.getCode() + " applied successfully!")
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Coupon is valid", result));
        } catch (Exception e) {
            CouponValidationResultDto result = CouponValidationResultDto.builder()
                    .valid(false)
                    .code(req.getCode())
                    .discountAmount(BigDecimal.ZERO)
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.ok(ApiResponse.<CouponValidationResultDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(result)
                    .build());
        }
    }

    @Operation(summary = "Create new discount coupon (Admin)")
    @PostMapping
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> create(@Valid @RequestBody CouponRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Coupon created successfully", couponService.createCoupon(req)));
    }
}
