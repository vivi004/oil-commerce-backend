package com.oilcommerce.coupon.service;

import com.oilcommerce.cart.entity.CartItem;
import com.oilcommerce.coupon.dto.*; import com.oilcommerce.coupon.entity.*;
import com.oilcommerce.coupon.repository.CouponRepository;
import com.oilcommerce.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal; import java.time.Instant; import java.util.List;

@Service @RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    public BigDecimal validateAndCalculateDiscount(String code, List<CartItem> items) {
        Coupon coupon = couponRepository.findByCodeAndActiveAndDeletedFalse(code.toUpperCase(), true)
                .orElseThrow(() -> new BusinessException("Invalid or expired coupon code"));

        if (coupon.getValidUntil() != null && coupon.getValidUntil().isBefore(Instant.now()))
            throw new BusinessException("Coupon has expired");
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit())
            throw new BusinessException("Coupon usage limit reached");

        BigDecimal subtotal = items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (coupon.getMinimumOrderAmount() != null && subtotal.compareTo(coupon.getMinimumOrderAmount()) < 0)
            throw new BusinessException("Minimum order amount not met. Minimum: " + coupon.getMinimumOrderAmount());

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getType() == CouponType.PERCENT) {
            discount = subtotal.multiply(coupon.getValue()).divide(BigDecimal.valueOf(100));
            if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0)
                discount = coupon.getMaximumDiscount();
        } else if (coupon.getType() == CouponType.FIXED) {
            discount = coupon.getValue();
        }
        return discount;
    }

    @Transactional
    public CouponDto createCoupon(CouponRequest req) {
        Coupon c = Coupon.builder().code(req.getCode().toUpperCase())
            .description(req.getDescription())
            .type(CouponType.valueOf(req.getType()))
            .value(req.getValue()).minimumOrderAmount(req.getMinimumOrderAmount())
            .maximumDiscount(req.getMaximumDiscount()).usageLimit(req.getUsageLimit())
            .active(req.isActive()).build();
        return toDto(couponRepository.save(c));
    }

    private CouponDto toDto(Coupon c) {
        return CouponDto.builder().id(c.getId()).code(c.getCode()).description(c.getDescription())
            .type(c.getType().name()).value(c.getValue()).minimumOrderAmount(c.getMinimumOrderAmount())
            .maximumDiscount(c.getMaximumDiscount()).usageLimit(c.getUsageLimit()).usageCount(c.getUsageCount())
            .isActive(c.isActive()).build();
    }
}
