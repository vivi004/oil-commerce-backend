package com.oilcommerce.review.entity;
import com.oilcommerce.common.BaseEntity; import com.oilcommerce.product.entity.Product; import com.oilcommerce.user.entity.User;
import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal;

@Entity @Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","product_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "product_id", nullable = false) private Product product;
    @Column(nullable = false, precision = 2, scale = 1) private BigDecimal rating;
    @Column private String title;
    @Column(columnDefinition = "TEXT") private String body;
    @Column @Builder.Default private int helpfulCount = 0;
    @Column @Builder.Default private boolean verified = false;
    @Column @Builder.Default private boolean approved = true;
}
