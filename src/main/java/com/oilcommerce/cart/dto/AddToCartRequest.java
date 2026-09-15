package com.oilcommerce.cart.dto;
import jakarta.validation.constraints.*; import lombok.Data; import java.util.UUID;
@Data
public class AddToCartRequest {
    @NotNull private UUID productId;
    private UUID variantId;
    @Min(1) private int quantity = 1;
}
