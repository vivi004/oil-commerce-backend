package com.oilcommerce.cart.service;

import com.oilcommerce.cart.dto.*;
import com.oilcommerce.cart.entity.CartItem;
import com.oilcommerce.cart.repository.CartRepository;
import com.oilcommerce.coupon.service.CouponService;
import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.product.entity.Product;
import com.oilcommerce.product.entity.ProductVariant;
import com.oilcommerce.product.repository.ProductRepository;
import com.oilcommerce.user.entity.User;
import com.oilcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;

    public CartDto getCart(UUID userId) {
        List<CartItem> items = cartRepository.findByUserIdAndDeletedFalse(userId);
        return buildCartDto(items, null, BigDecimal.ZERO);
    }

    @Transactional
    public CartDto addItem(UUID userId, AddToCartRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product","id",req.getProductId()));

        BigDecimal price = product.getPrice();
        ProductVariant variant = null;
        if (req.getVariantId() != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(req.getVariantId())).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Variant","id",req.getVariantId()));
            price = variant.getSellingPrice();
        }

        final BigDecimal finalPrice = price;
        final ProductVariant finalVariant = variant;

        cartRepository.findByUserIdAndVariantId(userId,
                variant != null ? variant.getId() : null).ifPresentOrElse(
            existing -> {
                existing.setQuantity(existing.getQuantity() + req.getQuantity());
                existing.setTotalPrice(finalPrice.multiply(BigDecimal.valueOf(existing.getQuantity())));
                cartRepository.save(existing);
            },
            () -> {
                CartItem item = CartItem.builder()
                        .user(user).product(product).variant(finalVariant)
                        .quantity(req.getQuantity()).unitPrice(finalPrice)
                        .totalPrice(finalPrice.multiply(BigDecimal.valueOf(req.getQuantity())))
                        .build();
                cartRepository.save(item);
            }
        );
        return getCart(userId);
    }

    @Transactional
    public CartDto updateItem(UUID userId, UUID itemId, UpdateCartRequest req) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem","id",itemId));
        if (!item.getUser().getId().equals(userId)) throw new BusinessException("Not your cart item");
        item.setQuantity(req.getQuantity());
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(req.getQuantity())));
        cartRepository.save(item);
        return getCart(userId);
    }

    @Transactional
    public CartDto removeItem(UUID userId, UUID itemId) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem","id",itemId));
        if (!item.getUser().getId().equals(userId)) throw new BusinessException("Not your cart item");
        item.setDeleted(true);
        cartRepository.save(item);
        return getCart(userId);
    }

    @Transactional
    public void clearCart(UUID userId) {
        cartRepository.findByUserIdAndDeletedFalse(userId).forEach(item -> {
            item.setDeleted(true); cartRepository.save(item);
        });
    }

    public CartDto applyCoupon(UUID userId, ApplyCouponRequest req) {
        List<CartItem> items = cartRepository.findByUserIdAndDeletedFalse(userId);
        BigDecimal discount = couponService.validateAndCalculateDiscount(req.getCouponCode(), items);
        return buildCartDto(items, req.getCouponCode(), discount);
    }

    private CartDto buildCartDto(List<CartItem> items, String coupon, BigDecimal discount) {
        BigDecimal subtotal = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<CartItemDto> dtos = items.stream().map(i -> CartItemDto.builder()
                .id(i.getId())
                .productId(i.getProduct().getId())
                .productName(i.getProduct().getName())
                .productImage(i.getProduct().getThumbnail())
                .productSlug(i.getProduct().getSlug())
                .variantId(i.getVariant() != null ? i.getVariant().getId() : null)
                .variantCode(i.getVariant() != null ? i.getVariant().getCode() : null)
                .variantLabel(i.getVariant() != null ? i.getVariant().getLabel() : null)
                .quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).totalPrice(i.getTotalPrice())
                .availableStock(i.getProduct().getStock())
                .build()).toList();
        return CartDto.builder()
                .items(dtos).itemCount(dtos.size())
                .subtotal(subtotal).discount(discount)
                .total(subtotal.subtract(discount))
                .couponCode(coupon).build();
    }
}
