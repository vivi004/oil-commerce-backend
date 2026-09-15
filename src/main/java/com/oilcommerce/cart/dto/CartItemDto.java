package com.oilcommerce.cart.dto;
import lombok.Builder; import lombok.Data;
import java.math.BigDecimal; import java.util.UUID;
@Data @Builder
public class CartItemDto {
    private UUID id; private UUID productId; private String productName;
    private String productImage; private String productSlug;
    private UUID variantId; private String variantCode; private String variantLabel;
    private int quantity; private BigDecimal unitPrice; private BigDecimal totalPrice;
    private int availableStock;
}
