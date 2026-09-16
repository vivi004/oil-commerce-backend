package com.oilcommerce.platformsettings.service;

import com.oilcommerce.exception.ResourceNotFoundException;
import com.oilcommerce.platformsettings.dto.MandiBenchmarkDto;
import com.oilcommerce.platformsettings.dto.PlatformSettingDto;
import com.oilcommerce.platformsettings.entity.MandiBenchmark;
import com.oilcommerce.platformsettings.entity.PlatformSetting;
import com.oilcommerce.platformsettings.repository.MandiBenchmarkRepository;
import com.oilcommerce.platformsettings.repository.PlatformSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformSettingsService {

    private final MandiBenchmarkRepository mandiBenchmarkRepository;
    private final PlatformSettingRepository platformSettingRepository;

    @Transactional(readOnly = true)
    public List<MandiBenchmarkDto> getAllMandiBenchmarks() {
        return mandiBenchmarkRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(this::mapToBenchmarkDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MandiBenchmarkDto createMandiBenchmark(MandiBenchmarkDto dto) {
        MandiBenchmark benchmark = MandiBenchmark.builder()
                .commodity(dto.getCommodity())
                .hsnCode(dto.getHsnCode() != null ? dto.getHsnCode() : "1202")
                .cropSeason(dto.getCropSeason() != null ? dto.getCropSeason() : "Rabi")
                .mandiBenchmarkRate(dto.getMandiBenchmarkRate() != null ? BigDecimal.valueOf(dto.getMandiBenchmarkRate()) : BigDecimal.ZERO)
                .edibleClassification(dto.getEdibleClassification() != null ? dto.getEdibleClassification() : "Edible Oilseed")
                .gstBracket(dto.getGstBracket() != null ? BigDecimal.valueOf(dto.getGstBracket()) : BigDecimal.valueOf(5.0))
                .effectiveDate(dto.getEffectiveDate() != null ? dto.getEffectiveDate() : "Today")
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .build();

        benchmark = mandiBenchmarkRepository.save(benchmark);
        return mapToBenchmarkDto(benchmark);
    }

    @Transactional
    public MandiBenchmarkDto updateMandiBenchmark(UUID id, MandiBenchmarkDto dto) {
        MandiBenchmark benchmark = mandiBenchmarkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MandiBenchmark", "id", id));

        if (dto.getCommodity() != null) benchmark.setCommodity(dto.getCommodity());
        if (dto.getHsnCode() != null) benchmark.setHsnCode(dto.getHsnCode());
        if (dto.getCropSeason() != null) benchmark.setCropSeason(dto.getCropSeason());
        if (dto.getMandiBenchmarkRate() != null) benchmark.setMandiBenchmarkRate(BigDecimal.valueOf(dto.getMandiBenchmarkRate()));
        if (dto.getEdibleClassification() != null) benchmark.setEdibleClassification(dto.getEdibleClassification());
        if (dto.getGstBracket() != null) benchmark.setGstBracket(BigDecimal.valueOf(dto.getGstBracket()));
        if (dto.getEffectiveDate() != null) benchmark.setEffectiveDate(dto.getEffectiveDate());
        if (dto.getStatus() != null) benchmark.setStatus(dto.getStatus());

        benchmark = mandiBenchmarkRepository.save(benchmark);
        return mapToBenchmarkDto(benchmark);
    }

    @Transactional
    public void deleteMandiBenchmark(UUID id) {
        mandiBenchmarkRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PlatformSettingDto getPlatformSettings() {
        PlatformSetting setting = platformSettingRepository.findFirstByOrderByCreatedAtDesc()
                .orElseGet(() -> PlatformSetting.builder()
                        .edibleOilGst(BigDecimal.valueOf(5.00))
                        .lampOilGst(BigDecimal.valueOf(18.00))
                        .mandiSheetUrl("https://docs.google.com/spreadsheets/d/1XyZ-SeedMarket-DailyPriceSync-2026/feed")
                        .autoSyncEnabled(true)
                        .build());

        return mapToSettingDto(setting);
    }

    @Transactional
    public PlatformSettingDto updatePlatformSettings(PlatformSettingDto dto) {
        PlatformSetting setting = platformSettingRepository.findFirstByOrderByCreatedAtDesc()
                .orElseGet(PlatformSetting::new);

        if (dto.getEdibleOilGst() != null) setting.setEdibleOilGst(BigDecimal.valueOf(dto.getEdibleOilGst()));
        if (dto.getLampOilGst() != null) setting.setLampOilGst(BigDecimal.valueOf(dto.getLampOilGst()));
        if (dto.getMandiSheetUrl() != null) setting.setMandiSheetUrl(dto.getMandiSheetUrl());
        setting.setAutoSyncEnabled(dto.isAutoSyncEnabled());

        setting = platformSettingRepository.save(setting);
        return mapToSettingDto(setting);
    }

    private MandiBenchmarkDto mapToBenchmarkDto(MandiBenchmark b) {
        return MandiBenchmarkDto.builder()
                .id(b.getId())
                .commodity(b.getCommodity())
                .hsnCode(b.getHsnCode())
                .cropSeason(b.getCropSeason())
                .mandiBenchmarkRate(b.getMandiBenchmarkRate() != null ? b.getMandiBenchmarkRate().doubleValue() : 0.0)
                .edibleClassification(b.getEdibleClassification())
                .gstBracket(b.getGstBracket() != null ? b.getGstBracket().doubleValue() : 5.0)
                .effectiveDate(b.getEffectiveDate())
                .status(b.getStatus())
                .build();
    }

    private PlatformSettingDto mapToSettingDto(PlatformSetting s) {
        return PlatformSettingDto.builder()
                .id(s.getId())
                .edibleOilGst(s.getEdibleOilGst() != null ? s.getEdibleOilGst().doubleValue() : 5.0)
                .lampOilGst(s.getLampOilGst() != null ? s.getLampOilGst().doubleValue() : 18.0)
                .mandiSheetUrl(s.getMandiSheetUrl())
                .autoSyncEnabled(s.isAutoSyncEnabled())
                .build();
    }
}
