package com.oilcommerce.brand.controller;

import com.oilcommerce.brand.dto.*; import com.oilcommerce.brand.service.BrandService;
import com.oilcommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.UUID;

@Tag(name = "Brands")
@RestController
@RequestMapping({"/brands", "/admin/brands"})
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAllBrands()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BrandDto>> create(@Valid @RequestBody BrandRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Brand created", brandService.createBrand(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandDto>> update(@PathVariable UUID id, @Valid @RequestBody BrandRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Brand updated", brandService.updateBrand(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted", null));
    }
}
