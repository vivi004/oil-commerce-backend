package com.oilcommerce.googlesheetsync.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import com.oilcommerce.googlesheetsync.service.GoogleSheetSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Sheet Sync")
@RestController
@RequestMapping("/admin/sheet-sync")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
public class SheetSyncController {

    private final GoogleSheetSyncService sheetSyncService;

    /**
     * Fetch the Google Sheet and compute price diffs against live DB variants.
     */
    @Operation(summary = "Import & diff prices from Google Sheet")
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<List<SheetSyncPreviewDto>>> importFromSheet(
            @RequestParam(required = false) String sheetUrl) {
        List<SheetSyncPreviewDto> diffs = sheetSyncService.importFromSheet(sheetUrl);
        return ResponseEntity.ok(ApiResponse.success(
                "Sheet import complete. Found " + diffs.size() + " price diff(s).", diffs));
    }

    /**
     * Return the cached preview from the last import call.
     */
    @Operation(summary = "Get cached sheet diff preview")
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<List<SheetSyncPreviewDto>>> preview() {
        return ResponseEntity.ok(ApiResponse.success(sheetSyncService.getPreview()));
    }

    /**
     * Persist approved price changes to the database.
     *
     * Preferred body format (stateless — no server cache dependency):
     *   { "skuPrices": { "NPO-GNO-1L": 210.00, "NPO-SSO-500ML": 145.00 } }
     *
     * Legacy body format (still accepted for backwards compatibility):
     *   { "skus": ["NPO-GNO-1L", "NPO-SSO-500ML"] }
     */
    @Operation(summary = "Apply approved price changes to DB variants")
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@RequestBody Map<String, Object> body) {
        Map<String, BigDecimal> skuPrices = new LinkedHashMap<>();

        // --- Preferred: skuPrices map with prices embedded ---
        Object skuPricesRaw = body.get("skuPrices");
        if (skuPricesRaw instanceof Map<?, ?> rawMap) {
            for (Map.Entry<?, ?> e : rawMap.entrySet()) {
                if (e.getKey() instanceof String sku && e.getValue() != null) {
                    try {
                        skuPrices.put(sku, new BigDecimal(e.getValue().toString()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        // --- Legacy fallback: only SKU list, look up price from cached preview ---
        if (skuPrices.isEmpty()) {
            Object skusRaw = body.get("skus");
            if (skusRaw == null) skusRaw = body.get("productIds");
            if (skusRaw instanceof List<?> skuList) {
                List<SheetSyncPreviewDto> preview = sheetSyncService.getPreview();
                if (preview.isEmpty()) {
                    // Cache might be empty after server restart — re-import from sheet
                    preview = sheetSyncService.importFromSheet(null);
                }
                Map<String, BigDecimal> previewPrices = new LinkedHashMap<>();
                for (SheetSyncPreviewDto item : preview) {
                    if (item.getSku() != null && item.getNewPrice() != null) {
                        previewPrices.put(item.getSku().trim().toUpperCase(), item.getNewPrice());
                    }
                }
                for (Object s : skuList) {
                    if (s instanceof String sku) {
                        BigDecimal price = previewPrices.get(sku.trim().toUpperCase());
                        if (price != null) skuPrices.put(sku.trim(), price);
                    }
                }
            }
        }

        sheetSyncService.approveChanges(skuPrices);
        return ResponseEntity.ok(ApiResponse.success("Price changes applied to DB.", null));
    }

    /**
     * Return last sync timestamp for display in the admin UI.
     */
    @Operation(summary = "Get last sync status")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status() {
        long ts = sheetSyncService.getLastSyncTimestamp();
        String lastSynced = ts == 0 ? "Never" : Instant.ofEpochMilli(ts).toString();
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("lastSyncTimestamp", ts, "lastSyncedAt", lastSynced)));
    }
}
