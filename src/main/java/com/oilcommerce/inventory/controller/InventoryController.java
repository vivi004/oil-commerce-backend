package com.oilcommerce.inventory.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.inventory.dto.StockAdjustmentRequest;
import com.oilcommerce.inventory.dto.StockInventoryItemDto;
import com.oilcommerce.inventory.dto.StockMovementDto;
import com.oilcommerce.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory", description = "Mara Chekku warehouse inventory & stock ledger management")
@RestController
@RequestMapping({"/inventory", "/admin/inventory"})
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get all stock inventory items with real-time status")
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockInventoryItemDto>>> getAllStockItems() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAllStockItems()));
    }

    @Operation(summary = "Adjust inventory stock balance")
    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<StockInventoryItemDto>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request) {
        StockInventoryItemDto updated = inventoryService.adjustStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted successfully", updated));
    }

    @Operation(summary = "Get recent stock movements ledger")
    @GetMapping("/movements")
    public ResponseEntity<ApiResponse<List<StockMovementDto>>> getRecentMovements() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getRecentMovements()));
    }

    @Operation(summary = "Get stock movements for a specific product")
    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockMovementDto>>> getMovementsByProductId(
            @PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getMovementsByProductId(productId)));
    }
}
