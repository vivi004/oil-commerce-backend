package com.oilcommerce.review.service;
import com.oilcommerce.common.PaginatedResponse; import com.oilcommerce.exception.BusinessException;
import com.oilcommerce.exception.ResourceNotFoundException; import com.oilcommerce.product.entity.Product;
import com.oilcommerce.product.repository.ProductRepository; import com.oilcommerce.review.dto.*;
import com.oilcommerce.review.entity.Review; import com.oilcommerce.review.repository.ReviewRepository;
import com.oilcommerce.user.entity.User; import com.oilcommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*; import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; import java.util.UUID;

@Service @RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository; private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public PaginatedResponse<ReviewDto> getProductReviews(UUID productId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC,"createdAt"));
        var result = reviewRepository.findByProductIdAndApprovedAndDeletedFalseOrderByCreatedAtDesc(productId, true, pageable);
        return PaginatedResponse.of(result.map(this::toDto).getContent(), result.getTotalElements(), page, pageSize);
    }

    @Transactional
    public ReviewDto createReview(UUID userId, UUID productId, ReviewRequest req) {
        if (reviewRepository.existsByUserIdAndProductId(userId, productId))
            throw new BusinessException("You have already reviewed this product");
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User","id",userId));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product","id",productId));
        Review review = Review.builder().user(user).product(product).rating(req.getRating()).title(req.getTitle()).body(req.getBody()).build();
        return toDto(reviewRepository.save(review));
    }

    private ReviewDto toDto(Review r) {
        return ReviewDto.builder().id(r.getId()).userId(r.getUser().getId())
            .userName(r.getUser().getFirstName() + " " + r.getUser().getLastName())
            .userAvatar(r.getUser().getAvatar()).rating(r.getRating())
            .title(r.getTitle()).body(r.getBody()).helpfulCount(r.getHelpfulCount())
            .verified(r.isVerified()).createdAt(r.getCreatedAt()).build();
    }
}
