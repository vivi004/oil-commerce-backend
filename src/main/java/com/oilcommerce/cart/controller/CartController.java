package com.oilcommerce.cart.controller;

import com.oilcommerce.cart.dto.*;
import com.oilcommerce.cart.service.CartService;
import com.oilcommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(name = "Cart") @RestController @RequestMapping("/cart")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
public class CartController {
    private final CartService cartService;
    private UUID uid(UserDetails ud) { return UUID.fromString(ud.getUsername()); }

    @GetMapping public ResponseEntity<ApiResponse<CartDto>> getCart(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(uid(ud))));
    }
    @PostMapping("/items") public ResponseEntity<ApiResponse<CartDto>> addItem(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody AddToCartRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Item added",cartService.addItem(uid(ud),req)));
    }
    @PutMapping("/items/{itemId}") public ResponseEntity<ApiResponse<CartDto>> updateItem(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID itemId, @Valid @RequestBody UpdateCartRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Cart updated",cartService.updateItem(uid(ud),itemId,req)));
    }
    @DeleteMapping("/items/{itemId}") public ResponseEntity<ApiResponse<CartDto>> removeItem(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID itemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed",cartService.removeItem(uid(ud),itemId)));
    }
    @DeleteMapping("/clear") public ResponseEntity<ApiResponse<Void>> clear(@AuthenticationPrincipal UserDetails ud) {
        cartService.clearCart(uid(ud)); return ResponseEntity.ok(ApiResponse.success("Cart cleared",null));
    }
    @PostMapping("/coupon") public ResponseEntity<ApiResponse<CartDto>> applyCoupon(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody ApplyCouponRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Coupon applied",cartService.applyCoupon(uid(ud),req)));
    }
}
