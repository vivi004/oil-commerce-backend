package com.oilcommerce.review.repository;
import com.oilcommerce.review.entity.Review;
import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; import java.util.UUID;
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByProductIdAndApprovedAndDeletedFalseOrderByCreatedAtDesc(UUID productId, boolean approved, Pageable pageable);
    boolean existsByUserIdAndProductId(UUID userId, UUID productId);
}
