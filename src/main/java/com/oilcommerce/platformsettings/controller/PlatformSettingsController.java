package com.oilcommerce.platformsettings.controller;

import com.oilcommerce.common.ApiResponse;
import com.oilcommerce.platformsettings.dto.MandiBenchmarkDto;
import com.oilcommerce.platformsettings.dto.PlatformSettingDto;
import com.oilcommerce.platformsettings.service.PlatformSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Platform Settings & Mandi", description = "Global multi-tenant defaults, GST rates, and Mandi commodity benchmarks")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PlatformSettingsController {

    private final PlatformSettingsService platformSettingsService;

    @Operation(summary = "Get global platform settings and tax rates")
    @GetMapping({"/platform-settings", "/admin/platform-settings"})
    public ResponseEntity<ApiResponse<PlatformSettingDto>> getSettings() {
        return ResponseEntity.ok(ApiResponse.success(platformSettingsService.getPlatformSettings()));
    }

    @Operation(summary = "Update global platform settings and tax rates")
    @PutMapping({"/platform-settings", "/admin/platform-settings"})
    public ResponseEntity<ApiResponse<PlatformSettingDto>> updateSettings(@RequestBody PlatformSettingDto dto) {
        PlatformSettingDto updated = platformSettingsService.updatePlatformSettings(dto);
        return ResponseEntity.ok(ApiResponse.success("Platform settings updated successfully", updated));
    }

    @Operation(summary = "Get daily Mandi raw seed benchmarks")
    @GetMapping({"/mandi-benchmarks", "/admin/mandi-benchmarks"})
    public ResponseEntity<ApiResponse<List<MandiBenchmarkDto>>> getMandiBenchmarks() {
        return ResponseEntity.ok(ApiResponse.success(platformSettingsService.getAllMandiBenchmarks()));
    }

    @Operation(summary = "Create Mandi seed benchmark")
    @PostMapping({"/mandi-benchmarks", "/admin/mandi-benchmarks"})
    public ResponseEntity<ApiResponse<MandiBenchmarkDto>> createBenchmark(@RequestBody MandiBenchmarkDto dto) {
        MandiBenchmarkDto created = platformSettingsService.createMandiBenchmark(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Mandi benchmark created successfully", created));
    }

    @Operation(summary = "Update Mandi seed benchmark rate")
    @PutMapping({"/mandi-benchmarks/{id}", "/admin/mandi-benchmarks/{id}"})
    public ResponseEntity<ApiResponse<MandiBenchmarkDto>> updateBenchmark(
            @PathVariable UUID id, @RequestBody MandiBenchmarkDto dto) {
        MandiBenchmarkDto updated = platformSettingsService.updateMandiBenchmark(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Mandi benchmark updated successfully", updated));
    }

    @Operation(summary = "Delete Mandi seed benchmark")
    @DeleteMapping({"/mandi-benchmarks/{id}", "/admin/mandi-benchmarks/{id}"})
    public ResponseEntity<ApiResponse<Void>> deleteBenchmark(@PathVariable UUID id) {
        platformSettingsService.deleteMandiBenchmark(id);
        return ResponseEntity.ok(ApiResponse.success("Mandi benchmark removed", null));
    }
}
