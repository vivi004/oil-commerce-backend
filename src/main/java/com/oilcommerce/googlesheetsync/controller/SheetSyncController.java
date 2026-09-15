package com.oilcommerce.googlesheetsync.controller;
import com.oilcommerce.common.ApiResponse; import com.oilcommerce.googlesheetsync.dto.SheetSyncPreviewDto;
import com.oilcommerce.googlesheetsync.service.GoogleSheetSyncService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.Map;

@Tag(name="Sheet Sync") @RestController @RequestMapping("/admin/sheet-sync")
@RequiredArgsConstructor @SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('TENANT_ADMIN','SUPER_ADMIN')")
public class SheetSyncController {
    private final GoogleSheetSyncService sheetSyncService;

    @PostMapping("/import") public ResponseEntity<ApiResponse<List<SheetSyncPreviewDto>>> importFromSheet(@RequestParam String sheetUrl) {
        return ResponseEntity.ok(ApiResponse.success("Import initiated", sheetSyncService.importFromSheet(sheetUrl)));
    }
    @GetMapping("/preview") public ResponseEntity<ApiResponse<List<SheetSyncPreviewDto>>> preview() {
        return ResponseEntity.ok(ApiResponse.success(sheetSyncService.getPreview()));
    }
    @PostMapping("/approve") public ResponseEntity<ApiResponse<Void>> approve(@RequestBody Map<String,List<String>> body) {
        sheetSyncService.approveChanges(body.get("productIds")); return ResponseEntity.ok(ApiResponse.success("Changes approved",null));
    }
}
