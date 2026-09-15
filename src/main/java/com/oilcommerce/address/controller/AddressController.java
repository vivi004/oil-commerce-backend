package com.oilcommerce.address.controller;

import com.oilcommerce.address.dto.*; import com.oilcommerce.address.service.AddressService;
import com.oilcommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@Tag(name = "Addresses") @RestController @RequestMapping("/addresses")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
public class AddressController {
    private final AddressService addressService;
    private UUID uid(UserDetails ud) { return UUID.fromString(ud.getUsername()); }

    @GetMapping public ResponseEntity<ApiResponse<List<AddressDto>>> getAll(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success(addressService.getAddresses(uid(ud))));
    }
    @PostMapping public ResponseEntity<ApiResponse<AddressDto>> create(@AuthenticationPrincipal UserDetails ud, @Valid @RequestBody AddressRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Address created",addressService.createAddress(uid(ud),req)));
    }
    @PutMapping("/{id}") public ResponseEntity<ApiResponse<AddressDto>> update(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID id, @Valid @RequestBody AddressRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Address updated",addressService.updateAddress(uid(ud),id,req)));
    }
    @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        addressService.deleteAddress(uid(ud),id); return ResponseEntity.ok(ApiResponse.success("Address deleted",null));
    }
    @PutMapping("/{id}/default") public ResponseEntity<ApiResponse<AddressDto>> setDefault(@AuthenticationPrincipal UserDetails ud, @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success("Default address set",addressService.setDefault(uid(ud),id)));
    }
}
