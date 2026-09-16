package com.oilcommerce.platformsettings.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSettingDto {
    private UUID id;
    private Double edibleOilGst;
    private Double lampOilGst;
    private String mandiSheetUrl;
    private boolean autoSyncEnabled;
}
