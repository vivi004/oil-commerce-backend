package com.oilcommerce.wishlist.service;

import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.product.entity.Product; import com.oilcommerce.product.repository.ProductRepository;
import com.oilcommerce.user.entity.User; import com.oilcommerce.user.repository.UserRepository;
import com.oilcommerce.wishlist.dto.*; import com.oilcommerce.wishlist.entity.WishlistItem;
import com.oilcommerce.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List; import java.util.UUID;

@Service @RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository; private final ProductRepository productRepository;

    public List<WishlistItemDto> getWishlist(UUID userId) {
        return wishlistRepository.findByUserIdAndDeletedFalse(userId).stream().map(w -> WishlistItemDto.builder()
            .id(w.getId()).productId(w.getProduct().getId()).productName(w.getProduct().getName())
            .productImage(w.getProduct().getThumbnail()).price(w.getProduct().getPrice())
            .slug(w.getProduct().getSlug()).build()).toList();
    }

    @Transactional
    public WishlistItemDto addToWishlist(UUID userId, WishlistRequest req) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        Product product = productRepository.findById(req.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product","id",req.getProductId()));
        WishlistItem item = wishlistRepository.findByUserIdAndProductId(userId, req.getProductId())
            .orElseGet(() -> WishlistItem.builder().user(user).product(product).build());
        item.setDeleted(false);
        WishlistItem saved = wishlistRepository.save(item);
        return WishlistItemDto.builder().id(saved.getId()).productId(product.getId())
            .productName(product.getName()).productImage(product.getThumbnail()).price(product.getPrice()).slug(product.getSlug()).build();
    }

    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        wishlistRepository.findByUserIdAndProductId(userId, productId).ifPresent(w -> {
            w.setDeleted(true); wishlistRepository.save(w);
        });
    }

    public boolean isInWishlist(UUID userId, UUID productId) {
        return wishlistRepository.existsByUserIdAndProductIdAndDeletedFalse(userId, productId);
    }
}
