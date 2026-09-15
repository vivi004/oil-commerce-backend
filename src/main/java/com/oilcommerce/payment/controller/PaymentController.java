package com.oilcommerce.payment.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.payment.dto.*;
import com.oilcommerce.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map; import java.util.UUID;

@Tag(name="Payments") @RestController @RequestMapping("/payment")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/methods") public ResponseEntity<ApiResponse<List<Map<String,String>>>> getMethods() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentMethods()));
    }
    @PostMapping("/initiate") public ResponseEntity<ApiResponse<PaymentInitiateResponse>> initiate(
            @AuthenticationPrincipal UserDetails ud, @Valid @RequestBody PaymentInitiateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.initiatePayment(UUID.fromString(ud.getUsername()), req)));
    }
    @PostMapping("/verify") public ResponseEntity<ApiResponse<Map<String,Boolean>>> verify(@Valid @RequestBody PaymentVerifyRequest req) {
        boolean ok = paymentService.verifyPayment(req);
        return ResponseEntity.ok(ApiResponse.success(Map.of("verified",ok)));
    }
}
