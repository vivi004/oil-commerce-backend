package com.oilcommerce.cart.repository;
import com.oilcommerce.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; import java.util.Optional; import java.util.UUID;
@Repository
public interface CartRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUserIdAndDeletedFalse(UUID userId);
    Optional<CartItem> findByUserIdAndVariantId(UUID userId, UUID variantId);
    void deleteByUserId(UUID userId);
    long countByUserIdAndDeletedFalse(UUID userId);
}
