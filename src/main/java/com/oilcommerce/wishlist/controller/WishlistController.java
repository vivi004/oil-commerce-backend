package com.oilcommerce.wishlist.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.wishlist.dto.*;
import com.oilcommerce.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;

@Tag(name="Wishlist") @RestController @RequestMapping("/wishlist")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
public class WishlistController {
    private final WishlistService wishlistService;
    private UUID uid(UserDetails ud) { return UUID.fromString(ud.getUsername()); }

    @GetMapping public ResponseEntity<ApiResponse<List<WishlistItemDto>>> get(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success(wishlistService.getWishlist(uid(ud))));
    }
    @PostMapping public ResponseEntity<ApiResponse<WishlistItemDto>> add(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody WishlistRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist",wishlistService.addToWishlist(uid(ud),req)));
    }
    @DeleteMapping("/{productId}") public ResponseEntity<ApiResponse<Void>> remove(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID productId) {
        wishlistService.removeFromWishlist(uid(ud),productId); return ResponseEntity.ok(ApiResponse.success("Removed from wishlist",null));
    }
    @GetMapping("/check/{productId}") public ResponseEntity<ApiResponse<Map<String,Boolean>>> check(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("inWishlist",wishlistService.isInWishlist(uid(ud),productId))));
    }
}
