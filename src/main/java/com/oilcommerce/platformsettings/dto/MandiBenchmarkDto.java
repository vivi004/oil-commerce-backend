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
public class MandiBenchmarkDto {
    private UUID id;
    private String commodity;
    private String hsnCode;
    private String cropSeason;
    private Double mandiBenchmarkRate;
    private String edibleClassification;
    private Double gstBracket;
    private String effectiveDate;
    private String status;
}
