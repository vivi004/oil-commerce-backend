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

import java.time.Instant;
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
     * Body: { "skus": ["NPO-GNO-1L", "NPO-SES-500ML", ...] }
     */
    @Operation(summary = "Apply approved price changes to DB variants")
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@RequestBody Map<String, List<String>> body) {
        List<String> skus = body.get("skus");
        // also accept legacy "productIds" key so older clients still work
        if (skus == null || skus.isEmpty()) {
            skus = body.get("productIds");
        }
        sheetSyncService.approveChanges(skus != null ? skus : List.of());
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
