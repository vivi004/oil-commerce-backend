package com.oilcommerce.review.dto;
import lombok.Builder; import lombok.Data; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Data @Builder
public class ReviewDto {
    private UUID id; private UUID userId; private String userName; private String userAvatar;
    private BigDecimal rating; private String title; private String body;
    private int helpfulCount; private boolean verified; private Instant createdAt;
}
