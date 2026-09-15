package com.oilcommerce.wishlist.dto;
import jakarta.validation.constraints.NotNull; import lombok.Data; import java.util.UUID;
@Data public class WishlistRequest { @NotNull private UUID productId; }
