package com.oilcommerce.wishlist.repository;
import com.oilcommerce.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {
    List<WishlistItem> findByUserIdAndDeletedFalse(UUID userId);
    Optional<WishlistItem> findByUserIdAndProductId(UUID userId, UUID productId);
    boolean existsByUserIdAndProductIdAndDeletedFalse(UUID userId, UUID productId);
}
