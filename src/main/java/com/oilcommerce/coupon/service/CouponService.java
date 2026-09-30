package com.oilcommerce.coupon.service;

import com.oilcommerce.cart.entity.CartItem;
import com.oilcommerce.coupon.dto.*;
import com.oilcommerce.coupon.entity.*;
import com.oilcommerce.coupon.repository.CouponRepository;
import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponDto getCouponByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException("Coupon code cannot be empty");
        }
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndDeletedFalse(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));
        return toDto(coupon);
    }

    public List<CouponDto> getActiveCoupons() {
        return couponRepository.findByActiveTrueAndDeletedFalse().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BigDecimal validateAndCalculateDiscount(String code, List<CartItem> items) {
        BigDecimal subtotal = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return calculateDiscountForAmount(code, subtotal);
    }

    public BigDecimal calculateDiscountForAmount(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;

        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndDeletedFalse(code.trim())
                .orElseThrow(() -> new BusinessException("Invalid coupon code: " + code));

        if (!coupon.isActive()) {
            throw new BusinessException("Coupon " + code + " is no longer active");
        }
        if (coupon.getValidUntil() != null && coupon.getValidUntil().isBefore(Instant.now())) {
            throw new BusinessException("Coupon " + code + " has expired");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new BusinessException("Coupon " + code + " usage limit reached");
        }
        if (coupon.getMinimumOrderAmount() != null && subtotal.compareTo(coupon.getMinimumOrderAmount()) < 0) {
            throw new BusinessException("Minimum order amount not met. Minimum: ₹" + coupon.getMinimumOrderAmount());
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getType() == CouponType.PERCENT) {
            discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
            if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discount = coupon.getMaximumDiscount();
            }
        } else if (coupon.getType() == CouponType.FIXED) {
            discount = coupon.getValue();
        }

        return discount.min(subtotal);
    }

    @Transactional
    public void recordCouponUsage(String code) {
        if (code == null || code.isBlank()) return;
        couponRepository.findByCodeIgnoreCaseAndDeletedFalse(code.trim()).ifPresent(c -> {
            c.setUsageCount(c.getUsageCount() + 1);
            couponRepository.save(c);
            log.info("Incremented usage count for coupon {} to {}", c.getCode(), c.getUsageCount());
        });
    }

    @Transactional
    public CouponDto createCoupon(CouponRequest req) {
        Coupon c = Coupon.builder()
                .code(req.getCode().toUpperCase().trim())
                .description(req.getDescription())
                .type(CouponType.valueOf(req.getType()))
                .value(req.getValue())
                .minimumOrderAmount(req.getMinimumOrderAmount())
                .maximumDiscount(req.getMaximumDiscount())
                .usageLimit(req.getUsageLimit())
                .active(req.isActive())
                .build();
        return toDto(couponRepository.save(c));
    }

    public CouponDto toDto(Coupon c) {
        return CouponDto.builder()
                .id(c.getId())
                .code(c.getCode())
                .description(c.getDescription())
                .type(c.getType().name())
                .value(c.getValue())
                .minimumOrderAmount(c.getMinimumOrderAmount())
                .maximumDiscount(c.getMaximumDiscount())
                .usageLimit(c.getUsageLimit())
                .usageCount(c.getUsageCount())
                .isActive(c.isActive())
                .build();
    }
}
