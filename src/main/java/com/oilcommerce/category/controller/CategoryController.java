package com.oilcommerce.category.controller;

import com.oilcommerce.category.dto.*;
import com.oilcommerce.category.service.CategoryService;
import com.oilcommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Categories", description = "Product category management")
@RestController
@RequestMapping({"/categories", "/admin/categories"})
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all active categories")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    @Operation(summary = "Get category tree (parent categories only)")
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getTree() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories()));
    }

    @Operation(summary = "Get category by ID or slug")
    @GetMapping("/{idOrSlug}")
    public ResponseEntity<ApiResponse<CategoryDto>> getByIdOrSlug(@PathVariable String idOrSlug) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryByIdOrSlug(idOrSlug)));
    }

    @Operation(summary = "Create new category")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created", categoryService.createCategory(request)));
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> update(
            @PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.updateCategory(id, request)));
    }

    @Operation(summary = "Delete category (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }
}
