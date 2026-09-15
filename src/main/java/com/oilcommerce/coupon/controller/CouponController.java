package com.oilcommerce.coupon.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.coupon.dto.*;
import com.oilcommerce.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name="Coupons") @RestController @RequestMapping("/coupons") @RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;
    @PostMapping @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CouponDto>> create(@Valid @RequestBody CouponRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Coupon created",couponService.createCoupon(req)));
    }
}
