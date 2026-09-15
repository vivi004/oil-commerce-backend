package com.oilcommerce.product.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.common.PaginatedResponse;
import com.oilcommerce.product.dto.*;
import com.oilcommerce.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Products", description = "Product catalogue endpoints")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get products with filters and pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductDto>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) List<String> brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize) {

        ProductFilterRequest filter = new ProductFilterRequest();
        filter.setSearch(search); filter.setCategoryId(categoryId); filter.setBrand(brand);
        filter.setMinPrice(minPrice); filter.setMaxPrice(maxPrice); filter.setMinRating(minRating);
        filter.setInStock(inStock); filter.setOnSale(onSale); filter.setSortBy(sortBy);
        filter.setPage(page); filter.setPageSize(pageSize);

        return ResponseEntity.ok(ApiResponse.success(productService.getProducts(filter)));
    }

    @Operation(summary = "Get featured products")
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getFeatured() {
        return ResponseEntity.ok(ApiResponse.success(productService.getFeaturedProducts()));
    }

    @Operation(summary = "Search products by keyword")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductDto>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(q, page, pageSize)));
    }

    @Operation(summary = "Get product by ID or slug")
    @GetMapping("/{idOrSlug}")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable String idOrSlug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductByIdOrSlug(idOrSlug)));
    }

    @Operation(summary = "Get related products")
    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getRelated(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getRelatedProducts(id)));
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER','TENANT_ADMIN','SUPER_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<ProductDto>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", productService.createProduct(request)));
    }

    @Operation(summary = "Update a product")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INVENTORY_MANAGER','TENANT_ADMIN','SUPER_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<ProductDto>> update(
            @PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request)));
    }

    @Operation(summary = "Delete a product (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
