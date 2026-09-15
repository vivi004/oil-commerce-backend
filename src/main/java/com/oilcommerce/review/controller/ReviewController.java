package com.oilcommerce.review.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.common.PaginatedResponse;
import com.oilcommerce.review.dto.*; import com.oilcommerce.review.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(name="Reviews") @RestController @RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<ApiResponse<PaginatedResponse<ReviewDto>>> get(@PathVariable UUID productId,
            @RequestParam(defaultValue="1") int page, @RequestParam(defaultValue="10") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(productId, page, pageSize)));
    }

    @PostMapping("/products/{productId}/reviews")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<ReviewDto>> create(@PathVariable UUID productId,
            @AuthenticationPrincipal UserDetails ud, @Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Review submitted",reviewService.createReview(UUID.fromString(ud.getUsername()), productId, req)));
    }
}
